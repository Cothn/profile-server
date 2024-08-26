/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.permissions;

import ru.dc.cms.commons.security.exception.PermissionException;
import ru.dc.cms.commons.security.permissions.Permission;
import ru.dc.cms.commons.security.permissions.PermissionResolver;
import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.api.TenantPermission;

/**
 * {@link ru.dc.cms.commons.security.permissions.PermissionResolver} for tenants.
 *
 * @author avasquez
 */
public class TenantPermissionResolver implements PermissionResolver<AccessToken, String> {

    @Override
    public Permission getGlobalPermission(AccessToken token) throws IllegalArgumentException, PermissionException {
        return getPermission(token, TenantPermission.ANY_TENANT);
    }

    @Override
    public Permission getPermission(AccessToken token, String tenantName) throws PermissionException {
        for (TenantPermission permission : token.getTenantPermissions()) {
            String permittedTenant = permission.getTenant();

            if (permittedTenant.equals(TenantPermission.ANY_TENANT) || permittedTenant.equals(tenantName)) {
                return permission;
            }
        }

        return null;
    }

}
