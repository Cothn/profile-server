/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.services;

import ru.dc.cms.profile.api.Profile;
import ru.dc.cms.profile.api.VerificationToken;
import ru.dc.cms.profile.api.exceptions.ProfileException;

/**
 * Service used to verify a particular activity with the profile owner (like a recently created profile or a reset
 * password request).
 *
 * @author avasquez
 */
public interface VerificationService {

    /**
     * Creates a new verification token. The token can be later transmitted to the client through email, for
     * example.
     *
     * @param profile the profile to create the token for
     */
    VerificationToken createToken(Profile profile) throws ProfileException;

    /**
     * Creates a verification token and sends the user an email with the token for verification.
     *
     * @param token             the verification token to send
     * @param profile           the profile of the user
     * @param verificationUrl   the URL the user should click to verify the new profile
     * @param from              the from address
     * @param subject           the subject of the email
     * @param templateName      the template name of the email
     *
     */
    void sendEmail(VerificationToken token, Profile profile, String verificationUrl, String from, String subject,
                   String templateName) throws ProfileException;

    /**
     * Returns the token that corresponds to the specified ID
     *
     * @param tokenId   the token ID, sent in the verification email
     *
     * @return the verification token object associated to the ID
     */
    VerificationToken getToken(String tokenId) throws ProfileException;

    /**
     * Deletes the token corresponding the specified ID.
     *
     * @param tokenId the ID of the token to delete
     */
    void deleteToken(String tokenId) throws ProfileException;

}
