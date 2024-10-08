/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.repositories.impl;

import com.mongodb.MongoException;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.dc.cms.commons.mongo.AbstractJongoRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.api.PersistentLogin;
import ru.dc.cms.profile.repositories.PersistentLoginRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Default implementation of {@link ru.dc.cms.profile.api.PersistentLogin}.
 *
 * @author avasquez
 */
public class PersistentLoginRepositoryImpl extends AbstractJongoRepository<PersistentLogin>
    implements PersistentLoginRepository {

    private static final Logger logger = LoggerFactory.getLogger(PersistentLoginRepositoryImpl.class);

    public static final String KEY_FIND_BY_PROFILE_ID_AND_TOKEN = "profile.persistentLogin.byProfileIdAndToken";
    public static final String KEY_REMOVE_TOKENS_OLDER_THAN_QUERY = "profile.persistentLogin.removeOlderThan";

    @Override
    public PersistentLogin findByProfileIdAndToken(String profileId, String token) throws MongoDataException {
        try {
            return getCollection().findOne(getQueryFor(KEY_FIND_BY_PROFILE_ID_AND_TOKEN)).as(PersistentLogin.class);
        } catch (MongoException ex) {
            String msg = "Unable to find persistent login by profile ID '" + profileId + "' and token '" + token + "'";
            logger.error(msg, ex);
            throw new MongoDataException(msg, ex);
        }
    }

    @Override
    public void removeOlderThan(long seconds) throws MongoDataException {
        long millis = TimeUnit.SECONDS.toMillis(seconds);
        Date limit = new Date(System.currentTimeMillis() - millis);

        remove(getQueryFor(KEY_REMOVE_TOKENS_OLDER_THAN_QUERY), limit);
    }

}
