/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown if no tenant with a specified name was found.
 *
 * @author avasquez
 */
public class NoSuchTenantException extends I10nProfileException {

    private static final String KEY = "profile.tenant.noSuchTenant";

    public NoSuchTenantException(String tenantName) {
        super(KEY, tenantName);
    }

}
