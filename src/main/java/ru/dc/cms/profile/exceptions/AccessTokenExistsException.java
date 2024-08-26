/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a create access token operation fails because an access token with the same properties already exists.
 *
 * @author avasquez
 */
public class AccessTokenExistsException extends I10nProfileException {

    public static final String KEY = "profile.accessToken.accessTokenExists";

    public AccessTokenExistsException(String tokenId) {
        super(KEY, tokenId);
    }


}
