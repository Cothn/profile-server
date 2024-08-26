/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories.impl;

import ru.dc.cms.commons.mongo.AbstractJongoRepository;
import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.repositories.AccessTokenRepository;

/**
 * Default implementation of {@link ru.dc.cms.profile.repositories.AccessTokenRepository}, using Jongo.
 *
 * @author avasquez
 */
public class AccessTokenRepositoryImpl extends AbstractJongoRepository<AccessToken> implements AccessTokenRepository {

}
