/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Throw when account had too much failed attempts.
 */
public class ProfileLockedException extends I10nProfileException {

    public static final String KEY = "profile.auth.lockedAccount";

    public ProfileLockedException() {
        super(KEY);
    }
}
