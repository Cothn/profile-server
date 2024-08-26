/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.controllers.rest;

import ru.dc.cms.commons.monitoring.rest.MonitoringRestControllerBase;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.beans.ConstructorProperties;

/**
 * Rest controller to provide monitoring information
 * @author joseross
 */
@RestController
@RequestMapping(MonitoringController.URL_ROOT)
public class MonitoringController extends MonitoringRestControllerBase {

    public final static String URL_ROOT = "/api/1";

    protected String authorizationToken;

    @ConstructorProperties({"authorizationToken"})
    public MonitoringController(final String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @Override
    public String getConfiguredToken() {
        return authorizationToken;
    }

}