/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.permissions;

import ru.dc.cms.commons.security.exception.PermissionException;
import ru.dc.cms.commons.security.permissions.Permission;
import ru.dc.cms.commons.security.permissions.PermissionResolver;
import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.api.AttributeDefinition;
import ru.dc.cms.profile.api.AttributePermission;

/**
 * {@link ru.dc.cms.commons.security.permissions.PermissionResolver} for attributes.
 *
 * @author avasquez
 */
public class AttributePermissionResolver implements PermissionResolver<AccessToken, AttributeDefinition> {

    @Override
    public Permission getGlobalPermission(AccessToken token) throws PermissionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Permission getPermission(AccessToken token, AttributeDefinition definition) throws PermissionException {
        for (AttributePermission permission : definition.getPermissions()) {
            String app = token.getApplication();
            String permittedApp = permission.getApplication();

            if (permittedApp.equals(AttributePermission.ANY_APPLICATION) || permittedApp.equals(app)) {
                return permission;
            }
        }

        return null;
    }

}
