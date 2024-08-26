/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories;

import ru.dc.cms.commons.mongo.CrudRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.api.Tenant;

/**
 * DB repository for {@link ru.dc.cms.profile.api.Tenant}s
 */
public interface TenantRepository extends CrudRepository<Tenant> {

    /**
     * Returns the tenant for the given name.
     *
     * @param name the tenant's name
     * @return the tenant, or null if not found.
     */
    Tenant findByName(String name) throws MongoDataException;

    /**
     * Removes the tenant for the given name.
     *
     * @param name  the tenant's name
     */
    void removeByName(String name) throws MongoDataException;

}
