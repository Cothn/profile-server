/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown if an attribute has already been defined.
 *
 * @author avasquez
 */
public class AttributeAlreadyDefinedException extends I10nProfileException {

    private static final String KEY = "profile.attribute.attributeAlreadyDefined";

    public AttributeAlreadyDefinedException(String attributeName, String tenant) {
        super(KEY, attributeName, tenant);
    }

}
