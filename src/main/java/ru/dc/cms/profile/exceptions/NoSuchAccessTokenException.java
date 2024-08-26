/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a specified access token ID doesn't correspond to any known access token.
 *
 * @author avasquez
 */
public class NoSuchAccessTokenException extends I10nProfileException {

    private static final String KEY = "profile.accessToken.noSuchAccessToken";

    public NoSuchAccessTokenException(String id) {
        super(KEY, id);
    }

}
