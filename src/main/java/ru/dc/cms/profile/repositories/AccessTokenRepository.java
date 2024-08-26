/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories;

import ru.dc.cms.commons.mongo.CrudRepository;
import ru.dc.cms.profile.api.AccessToken;

/**
 * Repository for storing {@link ru.dc.cms.profile.api.AccessToken}.
 *
 * @author avasquez
 */
public interface AccessTokenRepository extends CrudRepository<AccessToken> {
}
