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
