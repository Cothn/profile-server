/*
 * Copyright (C) 2007-2022 CMS Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
