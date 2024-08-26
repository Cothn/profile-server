/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when an attribute definition is being updated or when an attribute value is being set but not attribute
 * definition was found.
 *
 * @author avasquez
 */
public class AttributeNotDefinedException extends I10nProfileException {

    public static final String KEY = "profile.attribute.attributeNotDefined";

    public AttributeNotDefinedException(String attributeName, String tenant) {
        super(KEY, attributeName, tenant);
    }

}
