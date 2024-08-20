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
