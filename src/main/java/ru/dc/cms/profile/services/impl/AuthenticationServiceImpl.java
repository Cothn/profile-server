/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import ru.dc.cms.commons.crypto.CryptoUtils;
import ru.dc.cms.commons.i10n.I10nLogger;
import ru.dc.cms.commons.logging.Logged;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.commons.security.exception.ActionDeniedException;
import ru.dc.cms.commons.security.permissions.PermissionEvaluator;
import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.api.PersistentLogin;
import ru.dc.cms.profile.api.Profile;
import ru.dc.cms.profile.api.ProfileConstants;
import ru.dc.cms.profile.api.TenantAction;
import ru.dc.cms.profile.api.Ticket;
import ru.dc.cms.profile.api.exceptions.I10nProfileException;
import ru.dc.cms.profile.api.exceptions.ProfileException;
import ru.dc.cms.profile.api.services.AuthenticationService;
import ru.dc.cms.profile.api.services.ProfileService;
import ru.dc.cms.profile.exceptions.BadCredentialsException;
import ru.dc.cms.profile.exceptions.DisabledProfileException;
import ru.dc.cms.profile.exceptions.NoSuchPersistentLoginException;
import ru.dc.cms.profile.exceptions.NoSuchProfileException;
import ru.dc.cms.profile.exceptions.ProfileLockedException;
import ru.dc.cms.profile.repositories.PersistentLoginRepository;
import ru.dc.cms.profile.repositories.TicketRepository;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link ru.dc.cms.profile.api.services.AuthenticationService}.
 *
 * @author avasquez
 */
@Logged
public class AuthenticationServiceImpl implements AuthenticationService {

    private static final I10nLogger logger = new I10nLogger(AuthenticationServiceImpl.class,
                                                            "cms.profile.messages.logging");

    public static final String LOG_KEY_AUTHENTICATION_SUCCESSFUL = "profile.auth.authenticationSuccessful";
    public static final String LOG_KEY_TICKET_CREATED = "profile.auth.ticketCreated";
    public static final String LOG_KEY_TICKET_REQUESTED = "profile.auth.ticketRequested";
    public static final String LOG_KEY_TICKET_INVALIDATED = "profile.auth.tickedInvalidated";
    public static final String LOG_KEY_PERSISTENT_LOGIN_CREATED = "profile.auth.persistentLoginCreated";
    public static final String LOG_KEY_PERSISTENT_LOGIN_TOKEN_REFRESHED = "profile.auth.persistentLoginTokenRefreshed";
    public static final String LOG_KEY_PERSISTENT_LOGIN_DELETED = "profile.auth.persistentLoginDeleted";

    public static final String ERROR_KEY_CREATE_TICKET_ERROR = "profile.auth.createTicketError";
    public static final String ERROR_KEY_GET_TICKET_ERROR = "profile.auth.getTicketError";
    public static final String ERROR_KEY_UPDATE_TICKET_ERROR = "profile.auth.updateTicketError";
    public static final String ERROR_KEY_DELETE_TICKET_ERROR = "profile.auth.deleteTicketError";
    public static final String ERROR_KEY_CREATE_PERSISTENT_LOGIN_ERROR = "profile.auth.createdPersistentLoginError";
    public static final String ERROR_KEY_GET_PERSISTENT_LOGIN_ERROR = "profile.auth.getPersistentLoginError";
    public static final String ERROR_KEY_UPDATE_PERSISTENT_LOGIN_ERROR = "profile.auth.updatePersistentLoginError";
    public static final String ERROR_KEY_DELETE_PERSISTENT_LOGIN_ERROR = "profile.auth.deletePersistentLoginError";
    public static final String ERROR_KEY_WAIT_IS_ABORTED = "profile.auth.delayWasInterupt";

    protected PermissionEvaluator<AccessToken, String> permissionEvaluator;
    protected TicketRepository ticketRepository;
    protected PersistentLoginRepository persistentLoginRepository;
    protected ProfileService profileService;

    protected int lockTime;
    protected int failedLoginAttemptsBeforeLock;
    protected int failedLoginAttemptsBeforeDelay;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator<AccessToken, String> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Required
    public void setTicketRepository(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    @Required
    public void setPersistentLoginRepository(PersistentLoginRepository persistentLoginRepository) {
        this.persistentLoginRepository = persistentLoginRepository;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Override
    public Ticket authenticate(String tenantName, String username, String password) throws ProfileException {
        checkIfManageTicketsIsAllowed(tenantName);

        Profile profile = profileService.getProfileByUsername(tenantName, username, ProfileConstants.NO_ATTRIBUTE);


        if (profile == null) {
            // Invalid username
            throw new BadCredentialsException();
        }
        if (!profile.isEnabled()) {
            throw new DisabledProfileException(profile.getId().toString(), tenantName);
        }

        if (isProfileInTimeOut(profile)) {
            throw new ProfileLockedException();
        }

        try {
            if (!CryptoUtils.matchPassword(profile.getPassword(), password)) {
                // Invalid password
                countAsFail(profile);
                throw new BadCredentialsException();
            }

            clearAllLoginAttempts(profile);
            Ticket ticket = new Ticket();
            ticket.setId(UUID.randomUUID().toString());
            ticket.setTenant(tenantName);
            ticket.setProfileId(profile.getId().toString());
            ticket.setLastRequestTime(new Date());

            ticketRepository.insert(ticket);

            logger.debug(LOG_KEY_AUTHENTICATION_SUCCESSFUL, profile.getId(), ticket);

            return ticket;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TICKET_ERROR, profile.getId());
        }
    }

    private void clearAllLoginAttempts(final Profile profile) throws ProfileException {
        profile.setLastFailedLogin(new Date(0));
        profile.setFailedLoginAttempts(0);

        profileService.setFailedLoginAttempts(profile.getId().toString(), 0, ProfileConstants.NO_ATTRIBUTE);
    }

    protected void countAsFail(final Profile profile) throws ProfileException {
        profile.increaseFailedLoginAttempts();

        // This one counts !!!
        if ((failedLoginAttemptsBeforeDelay + failedLoginAttemptsBeforeLock) <= profile.getFailedLoginAttempts()) {
            profileService.setLastFailedLogin(profile.getId().toString(), new Date(), ProfileConstants.NO_ATTRIBUTE);
        }

        profileService.setFailedLoginAttempts(profile.getId().toString(), profile.getFailedLoginAttempts(),
                                              ProfileConstants.NO_ATTRIBUTE);
        if (profile.getFailedLoginAttempts() > failedLoginAttemptsBeforeDelay) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                logger.debug(ERROR_KEY_WAIT_IS_ABORTED);
            }
        }
    }

    protected boolean isProfileInTimeOut(final Profile profile) {
        if (profile.getLastFailedLogin() == null || profile.getFailedLoginAttempts() <= 0) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(profile.getLastFailedLogin());
        calendar.add(Calendar.MINUTE, lockTime);

        return new Date().before(calendar.getTime());
    }

    @Override
    public Ticket createTicket(String profileId) throws ProfileException {
        Profile profile = profileService.getProfile(profileId, ProfileConstants.NO_ATTRIBUTE);
        if (profile != null) {
            String tenantName = profile.getTenant();

            checkIfManageTicketsIsAllowed(tenantName);

            if (!profile.isEnabled()) {
                throw new DisabledProfileException(profile.getId().toString(), tenantName);
            }

            try {
                Ticket ticket = new Ticket();
                ticket.setId(UUID.randomUUID().toString());
                ticket.setTenant(tenantName);
                ticket.setProfileId(profile.getId().toString());
                ticket.setLastRequestTime(new Date());

                ticketRepository.insert(ticket);

                logger.debug(LOG_KEY_TICKET_CREATED, profile.getId(), ticket);

                return ticket;
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_CREATE_TICKET_ERROR, profile.getId());
            }
        } else {
            throw new NoSuchProfileException.ById(profileId);
        }
    }

    @Override
    public Ticket getTicket(String ticketId) throws ProfileException {
        Ticket ticket;
        try {
            ticket = ticketRepository.findByStringId(ticketId);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TICKET_ERROR, e, ticketId);
        }

        if (ticket != null) {
            checkIfManageTicketsIsAllowed(ticket.getTenant());

            ticket.setLastRequestTime(new Date());

            try {
                ticketRepository.save(ticket);
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_UPDATE_TICKET_ERROR, ticketId);
            }

            logger.debug(LOG_KEY_TICKET_REQUESTED, ticketId);

            return ticket;
        }

        return null;
    }

    @Override
    public void invalidateTicket(String ticketId) throws ProfileException {
        try {
            Ticket ticket = ticketRepository.findByStringId(ticketId);
            if (ticket != null) {
                checkIfManageTicketsIsAllowed(ticket.getTenant());

                ticketRepository.removeByStringId(ticketId);

                logger.debug(LOG_KEY_TICKET_INVALIDATED, ticketId);
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_TICKET_ERROR, ticketId);
        }
    }

    @Override
    public PersistentLogin createPersistentLogin(String profileId) throws ProfileException {
        Profile profile = profileService.getProfile(profileId, ProfileConstants.NO_ATTRIBUTE);
        if (profile != null) {
            String tenantName = profile.getTenant();

            checkIfManageTicketsIsAllowed(tenantName);

            if (!profile.isEnabled()) {
                throw new DisabledProfileException(profile.getId().toString(), tenantName);
            }

            try {
                PersistentLogin login = new PersistentLogin();
                login.setId(UUID.randomUUID().toString());
                login.setTenant(tenantName);
                login.setProfileId(profileId);
                login.setToken(UUID.randomUUID().toString());
                login.setTimestamp(new Date());

                persistentLoginRepository.insert(login);

                logger.debug(LOG_KEY_PERSISTENT_LOGIN_CREATED, profile.getId(), login);

                return login;
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_CREATE_PERSISTENT_LOGIN_ERROR, profile.getId());
            }
        } else {
            throw new NoSuchProfileException.ById(profileId);
        }
    }

    @Override
    public PersistentLogin getPersistentLogin(String loginId) throws ProfileException {
        try {
            PersistentLogin login = persistentLoginRepository.findByStringId(loginId);
            if (login != null) {
                checkIfManageTicketsIsAllowed(login.getTenant());
            }

            return login;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_PERSISTENT_LOGIN_ERROR, e, loginId);
        }
    }

    @Override
    public PersistentLogin refreshPersistentLoginToken(String loginId) throws ProfileException {
        PersistentLogin login = getPersistentLogin(loginId);
        if (login != null) {
            try {
                login.setToken(UUID.randomUUID().toString());

                persistentLoginRepository.save(login);

                logger.debug(LOG_KEY_PERSISTENT_LOGIN_TOKEN_REFRESHED, loginId, login.getToken());

                return login;
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_UPDATE_PERSISTENT_LOGIN_ERROR, loginId);
            }
        } else {
            throw new NoSuchPersistentLoginException(loginId);
        }
    }

    @Override
    public void deletePersistentLogin(String loginId) throws ProfileException {
        try {
            PersistentLogin login = persistentLoginRepository.findByStringId(loginId);
            if (login != null) {
                checkIfManageTicketsIsAllowed(login.getTenant());

                persistentLoginRepository.removeByStringId(loginId);

                logger.debug(LOG_KEY_PERSISTENT_LOGIN_DELETED, loginId);
            }
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_PERSISTENT_LOGIN_ERROR, loginId);
        }
    }

    protected void checkIfManageTicketsIsAllowed(String tenantName) {
        if (!permissionEvaluator.isAllowed(tenantName, TenantAction.MANAGE_TICKETS.toString())) {
            throw new ActionDeniedException(TenantAction.MANAGE_TICKETS.toString(), "tenant \"" + tenantName + "\"");
        }
    }


    public void setLockTime(final int lockTime) {
        this.lockTime = lockTime;
    }

    public void setFailedLoginAttemptsBeforeLock(int failedLoginAttemptsBeforeLock) {
        this.failedLoginAttemptsBeforeLock = failedLoginAttemptsBeforeLock;
    }

    public void setFailedLoginAttemptsBeforeDelay(int failedLoginAttemptsBeforeDelay) {
        this.failedLoginAttemptsBeforeDelay = failedLoginAttemptsBeforeDelay;
    }

}
