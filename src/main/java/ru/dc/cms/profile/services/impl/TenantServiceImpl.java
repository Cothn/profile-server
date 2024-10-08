/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.services.impl;

import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import ru.dc.cms.commons.collections.IterableUtils;
import ru.dc.cms.commons.entitlements.model.EntitlementType;
import ru.dc.cms.commons.entitlements.validator.EntitlementValidator;
import ru.dc.cms.commons.i10n.I10nLogger;
import ru.dc.cms.commons.logging.Logged;
import ru.dc.cms.commons.mongo.DuplicateKeyException;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.commons.mongo.UpdateHelper;
import ru.dc.cms.commons.security.exception.ActionDeniedException;
import ru.dc.cms.commons.security.permissions.PermissionEvaluator;
import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.api.AttributeDefinition;
import ru.dc.cms.profile.api.Tenant;
import ru.dc.cms.profile.api.TenantAction;
import ru.dc.cms.profile.api.exceptions.I10nProfileException;
import ru.dc.cms.profile.api.exceptions.ProfileException;
import ru.dc.cms.profile.api.services.ProfileService;
import ru.dc.cms.profile.api.services.TenantService;
import ru.dc.cms.profile.exceptions.NoSuchTenantException;
import ru.dc.cms.profile.exceptions.TenantExistsException;
import ru.dc.cms.profile.repositories.ProfileRepository;
import ru.dc.cms.profile.repositories.TenantRepository;
import ru.dc.cms.profile.utils.db.TenantUpdater;
import org.springframework.beans.factory.annotation.Required;

/**
 * Default implementation of {@link ru.dc.cms.profile.api.services.TenantService}.
 *
 * @author avasquez
 */
@Logged
public class TenantServiceImpl implements TenantService {

    private static final I10nLogger logger = new I10nLogger(TenantServiceImpl.class,
                                                            "cms.profile.messages.logging");

    public static final String LOG_KEY_TENANT_CREATED = "profile.tenant.tenantCreated";
    public static final String LOG_KEY_TENANT_DELETED = "profile.tenant.tenantDeleted";
    public static final String LOG_KEY_VERIFY_NEW_PROFILES_FLAG_SET = "profile.tenant.verifyNewProfilesFlagSet";
    public static final String LOG_KEY_ROLES_ADDED = "profile.tenant.rolesAdded";
    public static final String LOG_KEY_ROLES_REMOVED = "profile.tenant.rolesRemoved";
    public static final String LOG_KEY_ATTRIBUTE_DEFINITIONS_ADDED = "profile.tenant.attributeDefinitionsAdded";
    public static final String LOG_KEY_ATTRIBUTE_DEFINITIONS_UPDATED = "profile.tenant.attributeDefinitionsUpdated";
    public static final String LOG_KEY_ATTRIBUTE_DEFINITIONS_REMOVED = "profile.tenant.attributeDefinitionsRemoved";

    public static final String ERROR_KEY_CREATE_TENANT_ERROR = "profile.tenant.createTenantError";
    public static final String ERROR_KEY_GET_TENANT_ERROR = "profile.tenant.getTenantError";
    public static final String ERROR_KEY_UPDATE_TENANT_ERROR = "profile.tenant.updateTenantError";
    public static final String ERROR_KEY_DELETE_TENANT_ERROR = "profile.tenant.deleteTenantError";
    public static final String ERROR_KEY_GET_TENANT_COUNT_ERROR = "profile.tenant.getTenantCountError";
    public static final String ERROR_KEY_GET_ALL_TENANTS_ERROR = "profile.tenant.getAllTenantsError";

    public static final String ERROR_KEY_DELETE_ALL_PROFILES_ERROR = "profile.profile.deleteAll";
    public static final String ERROR_KEY_REMOVE_ROLE_FROM_ALL_PROFILES_ERROR = "profile.role.removeRoleFromAll";
    public static final String ERROR_KEY_REMOVE_ATTRIBUTE_FROM_ALL_PROFILES_ERROR = "profile.attribute" +
                                                                                    ".removeAttributeFromAllError";
    public static final String ERROR_KEY_ADD_DEFAULT_VALUE_ERROR = "profile.attribute.addDefaultValueError";

    public static final String ERROR_KEY_ENTITLEMENT_ERROR = "profile.license.entitlementError";

    protected PermissionEvaluator<AccessToken, String> tenantPermissionEvaluator;
    protected PermissionEvaluator<AccessToken, AttributeDefinition> attributePermissionEvaluator;
    protected TenantRepository tenantRepository;
    protected ProfileRepository profileRepository;
    protected ProfileService profileService;

    protected EntitlementValidator entitlementValidator;

    @Required
    public void setTenantPermissionEvaluator(PermissionEvaluator<AccessToken, String> tenantPermissionEvaluator) {
        this.tenantPermissionEvaluator = tenantPermissionEvaluator;
    }

    @Required
    public void setAttributePermissionEvaluator(
        PermissionEvaluator<AccessToken, AttributeDefinition> attributePermissionEvaluator) {
        this.attributePermissionEvaluator = attributePermissionEvaluator;
    }

    @Required
    public void setTenantRepository(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Required
    public void setProfileRepository(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Required
    public void setProfileService(ProfileService profileService) {
        this.profileService = profileService;
    }

    @Required
    public void setEntitlementValidator(final EntitlementValidator entitlementValidator) {
        this.entitlementValidator = entitlementValidator;
    }

    @Override
    public Tenant createTenant(Tenant tenant) throws ProfileException {
        checkIfTenantActionIsAllowed(null, TenantAction.CREATE_TENANT);

        try {
            entitlementValidator.validateEntitlement(EntitlementType.SITE, 1);
        } catch (Exception e) {
            throw new I10nProfileException(ERROR_KEY_ENTITLEMENT_ERROR, e);
        }

        // Make sure ID is null, we want it auto-generated
        tenant.setId(null);

        try {
            tenantRepository.insert(tenant);
        } catch (DuplicateKeyException e) {
            throw new TenantExistsException(tenant.getName());
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_CREATE_TENANT_ERROR, e, tenant.getName());
        }

        logger.debug(LOG_KEY_TENANT_CREATED, tenant);

        return tenant;
    }

    @Override
    public Tenant getTenant(String name) throws ProfileException {
        checkIfTenantActionIsAllowed(name, TenantAction.READ_TENANT);

        try {
            return tenantRepository.findByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_ERROR, e, name);
        }
    }

    @Override
    public Tenant updateTenant(final Tenant tenant) throws ProfileException {
        final String tenantName = tenant.getName();

        Tenant updatedTenant = updateTenant(tenant.getName(), new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                Tenant originalTenant = tenantUpdater.getTenant();
                Collection<String> originalRoles = originalTenant.getAvailableRoles();
                Collection<String> newRoles = tenant.getAvailableRoles();
                Collection<AttributeDefinition> originalDefinitions = originalTenant.getAttributeDefinitions();
                Collection<AttributeDefinition> newDefinitions = tenant.getAttributeDefinitions();
                Collection<String> removedRoles = CollectionUtils.subtract(originalRoles, newRoles);
                Collection<AttributeDefinition> removedDefinitions = CollectionUtils.subtract(originalDefinitions,
                                                                                              newDefinitions);

                for (String removedRole : removedRoles) {
                    removeRoleFromProfiles(tenantName, removedRole);
                }
                for (AttributeDefinition removedDefinition : removedDefinitions) {
                    removeAttributeFromProfiles(tenantName, removedDefinition.getName());
                }

                tenantUpdater.setVerifyNewProfiles(tenant.isVerifyNewProfiles());
                tenantUpdater.setSsoEnabled(tenant.isSsoEnabled());
                tenantUpdater.setCleanseAttributes(tenant.isCleanseAttributes());
                tenantUpdater.setAvailableRoles(tenant.getAvailableRoles());
                tenantUpdater.setAttributeDefinitions(tenant.getAttributeDefinitions());
            }

        });

        for (AttributeDefinition definition : updatedTenant.getAttributeDefinitions()) {
            addDefaultValue(tenantName, definition.getName(), definition.getDefaultValue());
        }

        return updatedTenant;
    }

    @Override
    public void deleteTenant(String name) throws ProfileException {
        checkIfTenantActionIsAllowed(name, TenantAction.DELETE_TENANT);

        try {
            profileRepository.removeAll(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_ALL_PROFILES_ERROR, e, name);
        }

        try {
            tenantRepository.removeByName(name);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_DELETE_TENANT_ERROR, e, name);
        }

        logger.debug(LOG_KEY_TENANT_DELETED, name);
    }

    @Override
    public long getTenantCount() throws ProfileException {
        checkIfTenantActionIsAllowed(null, TenantAction.READ_TENANT);

        try {
            return tenantRepository.count();
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_TENANT_COUNT_ERROR, e);
        }
    }

    @Override
    public List<Tenant> getAllTenants() throws ProfileException {
        checkIfTenantActionIsAllowed(null, TenantAction.READ_TENANT);

        try {
            return IterableUtils.toList(tenantRepository.findAll());
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_GET_ALL_TENANTS_ERROR, e);
        }
    }

    @Override
    public Tenant verifyNewProfiles(String tenantName, final boolean verify) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.setVerifyNewProfiles(verify);
            }

        });

        logger.debug(LOG_KEY_VERIFY_NEW_PROFILES_FLAG_SET, tenantName, verify);

        return tenant;
    }

    @Override
    public Tenant addRoles(String tenantName, final Collection<String> roles) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.addAvailableRoles(roles);
            }

        });

        logger.debug(LOG_KEY_ROLES_ADDED, roles, tenantName);

        return tenant;
    }

    @Override
    public Tenant removeRoles(final String tenantName, final Collection<String> roles) throws ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                for (String role : roles) {
                    removeRoleFromProfiles(tenantName, role);
                }

                tenantUpdater.removeAvailableRoles(roles);
            }

        });

        logger.debug(LOG_KEY_ROLES_REMOVED, roles, tenantName);

        return tenant;
    }

    @Override
    public Tenant addAttributeDefinitions(final String tenantName,
                                          final Collection<AttributeDefinition> attributeDefinitions) throws
        ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.addAttributeDefinitions(attributeDefinitions);
            }

        });

        for (AttributeDefinition definition : tenant.getAttributeDefinitions()) {
            addDefaultValue(tenantName, definition.getName(), definition.getDefaultValue());
        }

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_ADDED, attributeDefinitions, tenantName);

        return tenant;
    }

    @Override
    public Tenant updateAttributeDefinitions(final String tenantName,
                                             final Collection<AttributeDefinition> attributeDefinitions) throws
        ProfileException {
        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.updateAttributeDefinitions(attributeDefinitions);
            }

        });

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_UPDATED, attributeDefinitions, tenantName);

        return tenant;
    }

    @Override
    public Tenant removeAttributeDefinitions(final String tenantName,
                                             final Collection<String> attributeNames) throws ProfileException {
        for (String attributeName : attributeNames) {
            removeAttributeFromProfiles(tenantName, attributeName);
        }

        Tenant tenant = updateTenant(tenantName, new UpdateCallback() {

            @Override
            public void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException {
                tenantUpdater.removeAttributeDefinitions(attributeNames);
            }

        });

        logger.debug(LOG_KEY_ATTRIBUTE_DEFINITIONS_REMOVED, attributeNames, tenantName);

        return tenant;
    }

    protected void checkIfTenantActionIsAllowed(String tenantName, TenantAction action) {
        if (!tenantPermissionEvaluator.isAllowed(tenantName, action.toString())) {
            if (tenantName != null) {
                throw new ActionDeniedException(action.toString(), "tenant \"" + tenantName + "\"");
            } else {
                throw new ActionDeniedException(action.toString());
            }
        }
    }

    protected Tenant updateTenant(String tenantName, UpdateCallback callback) throws ProfileException {
        Tenant tenant = getTenant(tenantName);
        if (tenant != null) {
            checkIfTenantActionIsAllowed(tenantName, TenantAction.UPDATE_TENANT);

            UpdateHelper updateHelper = new UpdateHelper();
            TenantUpdater tenantUpdater = new TenantUpdater(tenant, updateHelper, tenantRepository);

            callback.doWithTenant(tenantUpdater);

            try {
                tenantUpdater.update();
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_UPDATE_TENANT_ERROR, e, tenant.getName());
            }
        } else {
            throw new NoSuchTenantException(tenantName);
        }

        return tenant;
    }

    protected interface UpdateCallback {

        void doWithTenant(TenantUpdater tenantUpdater) throws ProfileException;

    }

    protected void removeRoleFromProfiles(String tenantName, String role) throws ProfileException {
        try {
            profileRepository.removeRoleFromAll(tenantName, role);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_REMOVE_ROLE_FROM_ALL_PROFILES_ERROR, e, role, tenantName);
        }
    }

    protected void removeAttributeFromProfiles(String tenantName, String attributeName) throws ProfileException {
        try {
            profileRepository.removeAttributeFromAll(tenantName, attributeName);
        } catch (MongoDataException e) {
            throw new I10nProfileException(ERROR_KEY_REMOVE_ATTRIBUTE_FROM_ALL_PROFILES_ERROR, e, attributeName,
                                           tenantName);
        }
    }

    protected void addDefaultValue(String tenantName, String attributeName,
                                   Object defaultValue) throws ProfileException {
        if (defaultValue != null) {
            try {
                profileRepository.updateAllWithDefaultValue(tenantName, attributeName, defaultValue);
            } catch (MongoDataException e) {
                throw new I10nProfileException(ERROR_KEY_ADD_DEFAULT_VALUE_ERROR, e, attributeName, tenantName);
            }
        }
    }

}
