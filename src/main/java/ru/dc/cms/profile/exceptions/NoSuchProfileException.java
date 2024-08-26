/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown if no profile with a specified ID was found.
 *
 * @author avasquez
 */
public class NoSuchProfileException extends I10nProfileException {

    public static final String KEY_BY_ID = "profile.profile.noSuchProfileById";
    public static final String KEY_BY_QUERY = "profile.profile.noSuchProfileByQuery";
    public static final String KEY_BY_USERNAME = "profile.profile.noSuchProfileByUsername";
    public static final String KEY_BY_TICKET = "profile.profile.noSuchProfileByTicket";

    protected NoSuchProfileException(String key, Object... args) {
        super(key, args);
    }

    public static class ById extends NoSuchProfileException {

        public ById(String id) {
            super(KEY_BY_ID, id);
        }

    }

    public static class ByQuery extends NoSuchProfileException {

        public ByQuery(String tenantName, String query) {
            super(KEY_BY_QUERY, tenantName, query);
        }

    }

    public static class ByUsername extends NoSuchProfileException {

        public ByUsername(String tenantName, String query) {
            super(KEY_BY_USERNAME, tenantName, query);
        }

    }

    public static class ByTicket extends NoSuchProfileException {

        public ByTicket(String id) {
            super(KEY_BY_TICKET, id);
        }

    }

}
