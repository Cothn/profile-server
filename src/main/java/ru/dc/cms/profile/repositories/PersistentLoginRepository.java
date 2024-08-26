/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.repositories;

import ru.dc.cms.commons.mongo.CrudRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.api.PersistentLogin;

/**
 * DB repository for {@link ru.dc.cms.profile.api.PersistentLogin}s.
 *
 * @author avasquez
 */
public interface PersistentLoginRepository extends CrudRepository<PersistentLogin> {

    /**
     * Returns the login associated to the given profile ID and token.
     *
     * @param profileId the profile's ID
     * @param token     the token
     *
     * @return the login
     */
    PersistentLogin findByProfileIdAndToken(String profileId, String token) throws MongoDataException;

    /**
     * Removes logins with timestamps older than the specified number of seconds.
     *
     * @param seconds the number of seconds
     */
    void removeOlderThan(long seconds) throws MongoDataException;

}
