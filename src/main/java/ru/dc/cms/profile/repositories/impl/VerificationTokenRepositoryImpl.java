/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.repositories.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import ru.dc.cms.commons.mongo.AbstractJongoRepository;
import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.profile.repositories.VerificationTokenRepository;
import ru.dc.cms.profile.api.VerificationToken;

/**
 * Default implementation of {@link ru.dc.cms.profile.repositories.VerificationTokenRepository}.
 *
 * @author avasquez
 */
public class VerificationTokenRepositoryImpl extends AbstractJongoRepository<VerificationToken>
    implements VerificationTokenRepository {

    public static final String KEY_REMOVE_TOKENS_OLDER_THAN_QUERy = "profile.verificationToken.removeOlderThan";

    @Override
    public void removeOlderThan(long seconds) throws MongoDataException {
        long millis = TimeUnit.SECONDS.toMillis(seconds);
        Date limit = new Date(System.currentTimeMillis() - millis);

        remove(getQueryFor(KEY_REMOVE_TOKENS_OLDER_THAN_QUERy), limit);
    }

}
