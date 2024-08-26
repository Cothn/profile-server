/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories;

import ru.dc.cms.commons.mongo.CrudRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.api.Ticket;

/**
 * DB repository for {@link ru.dc.cms.profile.api.Ticket}s.
 *
 * @author avasquez
 */
public interface TicketRepository extends CrudRepository<Ticket> {

    /**
     * Removes tickets with last request time older than the specified number of seconds.
     *
     * @param seconds   the number of seconds
     */
    void removeWithLastRequestTimeOlderThan(long seconds) throws MongoDataException;

}
