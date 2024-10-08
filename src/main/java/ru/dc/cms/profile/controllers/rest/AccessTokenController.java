/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.controllers.rest;

import java.util.Collections;
import java.util.List;

import ru.dc.cms.profile.api.AccessToken;
import ru.dc.cms.profile.api.exceptions.ProfileException;
import ru.dc.cms.profile.api.services.AccessTokenService;
import ru.dc.cms.profile.exceptions.NoSuchAccessTokenException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static ru.dc.cms.profile.api.ProfileConstants.*;

/**
 * REST controller for the access token service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_ACCESS_TOKEN)
public class AccessTokenController {

    protected AccessTokenService accessTokenService;

    @Required
    public void setAccessTokenService(AccessTokenService accessTokenService) {
        this.accessTokenService = accessTokenService;
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_CREATE, method = RequestMethod.POST)
    @ResponseBody
    public AccessToken createToken(@RequestBody AccessToken token) throws ProfileException {
        return accessTokenService.createToken(token);
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_GET, method = RequestMethod.GET)
    @ResponseBody
    public AccessToken getToken(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        AccessToken token = accessTokenService.getToken(id);
        if (token != null) {
            return token;
        } else {
            throw new NoSuchAccessTokenException(id);
        }
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_GET_ALL, method = RequestMethod.GET)
    @ResponseBody
    public List<AccessToken> getAllTokens() throws ProfileException {
        List<AccessToken> tokens = accessTokenService.getAllTokens();
        if (tokens != null) {
            return tokens;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_ACCESS_TOKEN_DELETE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deleteToken(@PathVariable(PATH_VAR_ID) String id) throws ProfileException {
        accessTokenService.deleteToken(id);
    }

}
