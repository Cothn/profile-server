/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a username/password pair is invalid.
 *
 * @author avasquez
 */
public class BadCredentialsException extends I10nProfileException {

    public static final String KEY = "profile.auth.badCredentials";

    public BadCredentialsException() {
        super(KEY);
    }

}
