/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.entitlement;

import java.util.Arrays;
import java.util.List;

import ru.dc.cms.commons.entitlements.exception.UnsupportedEntitlementException;
import ru.dc.cms.commons.entitlements.model.EntitlementType;
import ru.dc.cms.commons.entitlements.model.Module;
import ru.dc.cms.commons.entitlements.usage.EntitlementUsageProvider;
import ru.dc.cms.profile.repositories.ProfileRepository;
import ru.dc.cms.profile.repositories.TenantRepository;
import org.springframework.beans.factory.annotation.Required;

import static ru.dc.cms.commons.entitlements.model.Module.PROFILE;

/**
 * Implementation of {@link EntitlementUsageProvider} for CMS Profile module.
 *
 * @author joseross
 */
public class ProfileLicenseUsageProvider implements EntitlementUsageProvider {

    /**
     * Current instance of {@link TenantRepository}.
     */
    protected TenantRepository tenantRepository;

    /**
     * Current instance of {@link ProfileRepository}.
     */
    protected ProfileRepository profileRepository;

    @Required
    public void setTenantRepository(final TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Required
    public void setProfileRepository(final ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Module getModule() {
        return PROFILE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EntitlementType> getSupportedEntitlements() {
        return Arrays.asList(EntitlementType.SITE, EntitlementType.USER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int doGetEntitlementUsage(final EntitlementType type) throws Exception {
        switch (type) {
            case SITE:
                return (int) tenantRepository.count();
            case USER:
                return (int) profileRepository.count();
            default:
                throw new UnsupportedEntitlementException(PROFILE, type);
        }
    }

}
