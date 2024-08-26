/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when an operation on a profile (like authentication) can't be performed because the profile is disabled.
 *
 * @author avasquez
 */
public class DisabledProfileException extends I10nProfileException {

    public static final String KEY = "profile.profile.disabledProfile";

    public DisabledProfileException(String id, String tenant) {
        super(KEY, id, tenant);
    }

}
