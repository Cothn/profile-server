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

import ru.dc.cms.commons.security.permissions.SubjectResolver;
import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.utils.AccessTokenUtils;

/**
 * {@link ru.dc.cms.commons.security.permissions.SubjectResolver} that resolves to the current
 * {@link ru.dc.cms.profile.api.AccessToken}.
 *
 * @author avasquez
 */
public class AccessTokenSubjectResolver implements SubjectResolver<AccessToken> {

    @Override
    public AccessToken getCurrentSubject() {
        return AccessTokenUtils.getCurrentToken();
    }

}
