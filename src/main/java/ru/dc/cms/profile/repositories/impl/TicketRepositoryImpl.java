/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.dc.cms.commons.mongo.AbstractJongoRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.api.Ticket;
import ru.dc.cms.profile.repositories.TicketRepository;

/**
 * Default implementation of {@link ru.dc.cms.profile.repositories.TicketRepository}.
 *
 * @author avasquez
 */
public class TicketRepositoryImpl extends AbstractJongoRepository<Ticket> implements TicketRepository {

    public static final String KEY_REMOVE_WITH_LAST_REQUEST_TIME_OLDER_THAN_QUERY = "profile.ticket" +
        ".removeWithLastRequestTimeOlderThan";

    @Override
    public void removeWithLastRequestTimeOlderThan(long seconds) throws MongoDataException {
        long millis = TimeUnit.SECONDS.toMillis(seconds);
        Date limit = new Date(System.currentTimeMillis() - millis);

        remove(getQueryFor(KEY_REMOVE_WITH_LAST_REQUEST_TIME_OLDER_THAN_QUERY), limit);
    }

}
