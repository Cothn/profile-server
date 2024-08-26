/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown if no persistent login with a specified ID was found.
 *
 * @author avasquez
 */
public class NoSuchPersistentLoginException extends I10nProfileException {

    public static final String KEY = "profile.auth.noSuchPersistentLogin";

    public NoSuchPersistentLoginException(String loginId) {
        super(KEY, loginId);
    }

}
