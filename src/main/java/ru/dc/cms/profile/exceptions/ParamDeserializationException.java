/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown the JSON in a param can't be correctly deserialized.
 *
 * @author avasquez
 */
public class ParamDeserializationException extends I10nProfileException {

    public static final String KEY = "profile.attribute.deserializationError";

    public ParamDeserializationException(Throwable cause) {
        super(KEY, cause);
    }

}
