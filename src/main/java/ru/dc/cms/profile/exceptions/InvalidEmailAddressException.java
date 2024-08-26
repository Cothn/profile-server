/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when an email address is in an invalid format.
 *
 * @author avasquez
 */
public class InvalidEmailAddressException extends I10nProfileException {

    private static final String KEY = "profile.profile.invalidEmailAddress";

    public InvalidEmailAddressException(String emailAddress) {
        super(KEY, emailAddress);
    }

}
