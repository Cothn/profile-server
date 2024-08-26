/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a create profile operation fails because a profile with the same tenant and username already exists.
 *
 * @author avasquez
 */
public class ProfileExistsException extends I10nProfileException {

    public static final String KEY = "profile.profile.profileExists";

    public ProfileExistsException(String tenantName, String username) {
        super(KEY, username, tenantName);
    }

}
