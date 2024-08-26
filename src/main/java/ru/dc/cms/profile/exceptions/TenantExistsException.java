/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown when a create tenant operation fails because a tenant with the same name already exists.
 *
 * @author avasquez
 */
public class TenantExistsException extends I10nProfileException {

    public static final String KEY = "profile.tenant.tenantExists";

    public TenantExistsException(String tenantName) {
        super(KEY, tenantName);
    }

}
