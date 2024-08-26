/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a serialized verification token is in an invalid format.
 *
 * @author avasquez
 */
public class NoSuchVerificationTokenException extends I10nProfileException {

    private static final String KEY = "profile.verification.noSuchVerificationToken";

    public NoSuchVerificationTokenException(String id) {
        super(KEY, id);
    }

}
