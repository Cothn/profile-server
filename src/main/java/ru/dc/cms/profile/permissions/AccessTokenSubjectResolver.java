/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
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
