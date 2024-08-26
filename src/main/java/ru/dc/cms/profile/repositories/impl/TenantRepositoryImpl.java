/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories.impl;

import ru.dc.cms.commons.mongo.AbstractJongoRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.api.Tenant;
import ru.dc.cms.profile.repositories.TenantRepository;

/**
 * Default implementation of {@link ru.dc.cms.profile.repositories.TenantRepository}.
 *
 * @author avasquez
 */
public class TenantRepositoryImpl extends AbstractJongoRepository<Tenant> implements TenantRepository {

    public static final String KEY_INDEX_KEYS =             "profile.tenant.index.keys";
    public static final String KEY_INDEX_OPTIONS =          "profile.tenant.index.options";
    public static final String KEY_FIND_BY_NAME_QUERY =     "profile.tenant.byName";
    public static final String KEY_REMOVE_BY_NAME_QUERY =   "profile.tenant.removeByName";

    public void init() throws Exception {
        super.init();

        getCollection().ensureIndex(getQueryFor(KEY_INDEX_KEYS), getQueryFor(KEY_INDEX_OPTIONS));
    }

    @Override
    public Tenant findByName(String name) throws MongoDataException {
        return findOne(getQueryFor(KEY_FIND_BY_NAME_QUERY), name);
    }

    @Override
    public void removeByName(String name) throws MongoDataException {
        remove(getQueryFor(KEY_REMOVE_BY_NAME_QUERY), name);
    }

}
