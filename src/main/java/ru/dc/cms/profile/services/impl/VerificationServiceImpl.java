/*
 * Copyright (C) 2007-2022 CMS Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.dc.cms.profile.services.impl;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import ru.dc.cms.commons.i10n.I10nLogger;
import ru.dc.cms.commons.logging.Logged;
import ru.dc.cms.commons.mail.EmailException;
import ru.dc.cms.commons.mail.EmailFactory;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.commons.security.exception.ActionDeniedException;
import ru.dc.cms.commons.security.permissions.PermissionEvaluator;
import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.api.Profile;
import ru.dc.cms.profile.api.ProfileConstants;
import ru.dc.cms.profile.api.TenantAction;
import ru.dc.cms.profile.api.VerificationToken;
import ru.dc.cms.profile.api.exceptions.I10nProfileException;
import ru.dc.cms.profile.api.exceptions.ProfileException;
import ru.dc.cms.profile.repositories.VerificationTokenRepository;
import ru.dc.cms.profile.services.VerificationService;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.annotation.Async;

/**
 * Default implementation of {@link ru.dc.cms.profile.services.VerificationService}.
 *
 * @author avasquez
 */
@Logged
public class VerificationServiceImpl implements VerificationService {

    private static final I10nLogger logger = new I10nLogger(VerificationServiceImpl.class,
                                                            "cms.profile.messages.logging");

    public static final String VERIFICATION_LINK_TEMPLATE_ARG = "verificationLink";

    public static final String LOG_KEY_TOKEN_CREATED = "profile.verification.tokenCreated";
    public static final String LOG_KEY_EMAIL_SENT = "profile.verification.emailSent";
    public static final String LOG_KEY_TOKEN_DELETED = "profile.verification.tokenDeleted";
    public static final String ERROR_KEY_CREATE_TOKEN_ERROR = "profile.verification.createTokenError";
    public static final String ERROR_KEY_GET_TOKEN_ERROR = "profile.verification.getTokenError";
    public static final String ERROR_KEY_DELETE_TOKEN_ERROR = "profile.verification.deleteTokenError";
    public static final String ERROR_KEY_EMAIL_ERROR = "profile.verification.emailError";

    protected PermissionEvaluator<AccessToken, String> permissionEvaluator;
    protected VerificationTokenRepository tokenRepository;
    protected EmailFactory emailFactory;
    protected int tokenMaxAge;

    @Required
    public void setPermissionEvaluator(PermissionEvaluator<AccessToken, String> permissionEvaluator) {
        this.permissionEvaluator = permissionEvaluator;
    }

    @Required
    public void setTokenRepository(VerificationTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Required
    public void setEmailFactory(EmailFactory emailFactory) {
        this.emailFactory = emailFactory;
    }

    @Required
    public void setTokenMaxAge(int tokenMaxAge) {
        this.tokenMaxAge = tokenMaxAge;
    }

    @Override
    public VerificationToken createToken(Profile profile) throws ProfileException {
        String tenant = profile.getTenant();
        String profileId = profile.getId().toString();

        VerificationToken token = new VerificationToken();
        token.setId(UUID.randomUUID().toString());
        token.setTenant(tenant);
        token.setProfileId(profileId);
        token.setTimestamp(new Date());

        try {
            tokenRepository.insert(token);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TOKEN_ERROR, profileId);
        }

        logger.debug(LOG_KEY_TOKEN_CREATED, profileId, token);

        return token;
    }

    @Override
    @Async
    public void sendEmail(VerificationToken token, Profile profile, String verificationBaseUrl, String from,
                          String subject, String templateName) throws ProfileException {
        String verificationUrl = createVerificationUrl(verificationBaseUrl, token.getId().toString());

        Map<String, String> templateArgs = Collections.singletonMap(VERIFICATION_LINK_TEMPLATE_ARG, verificationUrl);
        String[] to = new String[] {profile.getEmail()};

        try {
            emailFactory.getEmail(from, to, null, null, subject, templateName, templateArgs, true).send();

            logger.debug(LOG_KEY_EMAIL_SENT, profile.getId(), profile.getEmail());
        } catch (EmailException e) {
            throw new I10nProfileException(ERROR_KEY_EMAIL_ERROR, e, profile.getEmail());
        }
    }

    @Override
    public VerificationToken getToken(String tokenId) throws ProfileException {
        try {
            VerificationToken token = tokenRepository.findByStringId(tokenId);
            if (token != null) {
                checkIfManageProfilesIsAllowed(token.getTenant());
            }

            return token;
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TOKEN_ERROR, tokenId);
        }
    }

    @Override
    public void deleteToken(String tokenId) throws ProfileException {
        VerificationToken token = getToken(tokenId);
        if (token != null) {
            try {
                tokenRepository.removeByStringId(tokenId);
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_DELETE_TOKEN_ERROR, tokenId);
            }

            logger.debug(LOG_KEY_TOKEN_DELETED, tokenId);
        }
    }

    protected String createVerificationUrl(String verificationBaseUrl, String tokenId) {
        StringBuilder verificationUrl = new StringBuilder(verificationBaseUrl);

        if (verificationBaseUrl.contains("?")) {
            verificationUrl.append("&");
        } else {
            verificationUrl.append("?");
        }

        verificationUrl.append(ProfileConstants.PARAM_TOKEN_ID).append("=").append(tokenId);

        return verificationUrl.toString();
    }

    protected void checkIfManageProfilesIsAllowed(String tenantName) {
        if (!permissionEvaluator.isAllowed(tenantName, TenantAction.MANAGE_PROFILES.toString())) {
            throw new ActionDeniedException(TenantAction.MANAGE_PROFILES.toString(), "tenant \"" + tenantName + "\"");
        }
    }

}
