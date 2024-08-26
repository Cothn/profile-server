/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.exceptions;

import ru.dc.cms.profile.api.exceptions.I10nProfileException;

/**
 * Thrown if no ticket with a specified ID was found.
 *
 * @author avasquez
 */
public class NoSuchTicketException extends I10nProfileException {

    public static final String KEY = "profile.auth.noSuchTicket";

    public NoSuchTicketException(String ticketId) {
        super(KEY, ticketId);
    }

    public static class Expired extends NoSuchTicketException {

        public Expired(String ticketId) {
            super(ticketId);
        }

    }

}
