/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.utils;

import javax.servlet.http.HttpServletRequest;

import ru.dc.cms.commons.http.RequestContext;
import ru.dc.cms.profile.api.AccessToken;

/**
 * Utility methods for {@link ru.dc.cms.profile.api.AccessToken}s.
 *
 * @author avasquez
 */
public class AccessTokenUtils {

    public static final String ACCESS_TOKE_ATTRIBUTE_NAME = "accessToken";

    public static AccessToken getCurrentToken() {
        return getAccessToken(RequestContext.getCurrent().getRequest());
    }

    public static void setCurrentToken(AccessToken accessToken) {
        setAccessToken(RequestContext.getCurrent().getRequest(), accessToken);
    }

    public static AccessToken getAccessToken(HttpServletRequest request) {
        return (AccessToken)request.getAttribute(ACCESS_TOKE_ATTRIBUTE_NAME);
    }

    public static void setAccessToken(HttpServletRequest request, AccessToken accessToken) {
        request.setAttribute(ACCESS_TOKE_ATTRIBUTE_NAME, accessToken);
    }

    private AccessTokenUtils() {
    }

}
