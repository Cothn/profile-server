/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories;

import ru.dc.cms.commons.mongo.CrudRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.api.VerificationToken;

/**
 * Repository for {@link ru.dc.cms.profile.api.VerificationToken}s
 *
 * @author avasquez
 */
public interface VerificationTokenRepository extends CrudRepository<VerificationToken> {

    /**
     * Removes tokens with timestamps older than the specified number of seconds.
     *
     * @param seconds   the number of seconds
     */
    void removeOlderThan(long seconds) throws MongoDataException;

}
