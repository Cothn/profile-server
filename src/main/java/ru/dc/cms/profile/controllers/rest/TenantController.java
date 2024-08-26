/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */
package ru.dc.cms.profile.controllers.rest;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import ru.dc.cms.profile.api.AttributeDefinition;
import ru.dc.cms.profile.api.Tenant;
import ru.dc.cms.profile.api.exceptions.ProfileException;
import ru.dc.cms.profile.api.services.TenantService;
import ru.dc.cms.profile.exceptions.NoSuchTenantException;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import static ru.dc.cms.profile.api.ProfileConstants.BASE_URL_TENANT;
import static ru.dc.cms.profile.api.ProfileConstants.PARAM_ATTRIBUTE_NAME;
import static ru.dc.cms.profile.api.ProfileConstants.PARAM_ROLE;
import static ru.dc.cms.profile.api.ProfileConstants.PARAM_VERIFY;
import static ru.dc.cms.profile.api.ProfileConstants.PATH_VAR_NAME;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_ADD_ATTRIBUTE_DEFINITIONS;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_ADD_ROLES;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_COUNT;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_CREATE;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_DELETE;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_GET;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_GET_ALL;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_REMOVE_ATTRIBUTE_DEFINITIONS;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_REMOVE_ROLES;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_UPDATE;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_UPDATE_ATTRIBUTE_DEFINITIONS;
import static ru.dc.cms.profile.api.ProfileConstants.URL_TENANT_VERIFY_NEW_PROFILES;

/**
 * REST controller for the tenant service.
 *
 * @author avasquez
 */
@Controller
@RequestMapping(BASE_URL_TENANT)
public class TenantController {

    private TenantService tenantService;

    @Required
    public void setTenantService(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    @RequestMapping(value = URL_TENANT_CREATE, method = RequestMethod.POST)
    @ResponseBody
    public Tenant createTenant(@RequestBody Tenant tenant) throws ProfileException {
        return tenantService.createTenant(tenant);
    }

    @RequestMapping(value = URL_TENANT_GET, method = RequestMethod.GET)
    @ResponseBody
    public Tenant getTenant(@PathVariable(PATH_VAR_NAME) String name) throws ProfileException {
        Tenant tenant = tenantService.getTenant(name);
        if (tenant != null) {
            return tenant;
        } else {
            throw new NoSuchTenantException(name);
        }
    }

    @RequestMapping(value = URL_TENANT_UPDATE, method = RequestMethod.POST)
    @ResponseBody
    public Tenant updateTenant(@RequestBody Tenant tenant) throws ProfileException {
        return tenantService.updateTenant(tenant);
    }

    @RequestMapping(value = URL_TENANT_DELETE, method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void deleteTenant(@PathVariable(PATH_VAR_NAME) String name) throws ProfileException {
        tenantService.deleteTenant(name);
    }

    @RequestMapping(value = URL_TENANT_COUNT, method = RequestMethod.GET)
    @ResponseBody
    public long getTenantCount() throws ProfileException {
        return tenantService.getTenantCount();
    }

    @RequestMapping(value = URL_TENANT_GET_ALL, method = RequestMethod.GET)
    @ResponseBody
    public List<Tenant> getAllTenants() throws ProfileException {
        List<Tenant> tenants = tenantService.getAllTenants();
        if (tenants != null) {
            return tenants;
        } else {
            return Collections.emptyList();
        }
    }

    @RequestMapping(value = URL_TENANT_VERIFY_NEW_PROFILES, method = RequestMethod.POST)
    @ResponseBody
    public Tenant verifyNewProfiles(@PathVariable(PATH_VAR_NAME) String tenantName,
                                    @RequestParam(PARAM_VERIFY) boolean verify) throws ProfileException {
        return tenantService.verifyNewProfiles(tenantName, verify);
    }

    @RequestMapping(value = URL_TENANT_ADD_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Tenant addRoles(@PathVariable(PATH_VAR_NAME) String tenantName,
                           @RequestParam(PARAM_ROLE) Collection<String> roles) throws ProfileException {
        return tenantService.addRoles(tenantName, roles);
    }

    @RequestMapping(value = URL_TENANT_REMOVE_ROLES, method = RequestMethod.POST)
    @ResponseBody
    public Tenant removeRoles(@PathVariable(PATH_VAR_NAME) String tenantName,
                              @RequestParam(PARAM_ROLE) Collection<String> roles) throws ProfileException {
        return tenantService.removeRoles(tenantName, roles);
    }

    @RequestMapping(value = URL_TENANT_ADD_ATTRIBUTE_DEFINITIONS, method = RequestMethod.POST)
    @ResponseBody
    public Tenant addAttributeDefinitions(@PathVariable(PATH_VAR_NAME) String tenantName,
                                          @RequestBody Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        return tenantService.addAttributeDefinitions(tenantName, attributeDefinitions);
    }

    @RequestMapping(value = URL_TENANT_UPDATE_ATTRIBUTE_DEFINITIONS, method = RequestMethod.POST)
    @ResponseBody
    public Tenant updateAttributeDefinitions(@PathVariable(PATH_VAR_NAME) String tenantName,
                                             @RequestBody Collection<AttributeDefinition> attributeDefinitions)
            throws ProfileException {
        return tenantService.updateAttributeDefinitions(tenantName, attributeDefinitions);
    }

    @RequestMapping(value = URL_TENANT_REMOVE_ATTRIBUTE_DEFINITIONS, method = RequestMethod.POST)
    @ResponseBody
    public Tenant removeAttributeDefinitions(@PathVariable(PATH_VAR_NAME) String tenantName,
                                             @RequestParam(PARAM_ATTRIBUTE_NAME) Collection<String> attributeNames)
            throws ProfileException {
        return tenantService.removeAttributeDefinitions(tenantName, attributeNames);
    }

}
