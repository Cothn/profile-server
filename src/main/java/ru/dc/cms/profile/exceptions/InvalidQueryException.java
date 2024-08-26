/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a specified Mongo query is invalid, because of illegal operators ($where) or non-readable attributes.
 *
 * @author avasquez
 */
public class InvalidQueryException extends I10nProfileException {

    public InvalidQueryException(String key, Object... args) {
        super(key, args);
    }

}
