/* Copyright (C) 2022-2024 Digital Chief Company. All Rights Reserved. */

package ru.dc.cms.profile.utils.db;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ru.dc.cms.commons.mongo.MongoDataException;
import ru.dc.cms.commons.mongo.UpdateHelper;
import ru.dc.cms.profile.api.Profile;
import ru.dc.cms.profile.repositories.ProfileRepository;

/**
 * Created by alfonsovasquez on 14/6/16.
 */
public class ProfileUpdater {
    
    protected Profile profile;
    protected UpdateHelper updateHelper;
    protected ProfileRepository profileRepository;

    public ProfileUpdater(Profile profile, UpdateHelper updateHelper, ProfileRepository profileRepository) {
        this.profile = profile;
        this.updateHelper = updateHelper;
        this.profileRepository = profileRepository;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setUsername(String username) {
        profile.setUsername(username);
        updateHelper.set("username", username);
    }

    public void setPassword(String password) {
        profile.setPassword(password);
        updateHelper.set("password", password);
    }

    public void setEmail(String email) {
        profile.setEmail(email);
        updateHelper.set("email", email);
    }

    public void setVerified(boolean verified) {
        profile.setVerified(verified);
        updateHelper.set("verified", verified);
    }

    public void setEnabled(boolean enabled) {
        profile.setEnabled(enabled);
        updateHelper.set("enabled", enabled);
    }

    public void setLastModified(Date lastModified) {
        profile.setLastModified(lastModified);
        updateHelper.set("lastModified", lastModified);
    }

    public void setRoles(Set<String> roles) {
        profile.setRoles(roles);
        updateHelper.set("roles", roles);
    }

    public void addRoles(Collection<String> roles) {
        Set<String> allRoles = profile.getRoles();
        List<String> pushValues = new ArrayList<>();

        for (String role : roles) {
            if (allRoles.add(role)) {
                pushValues.add(role);
            }
        }

        updateHelper.pushAll("roles", pushValues);
    }

    public void removeRoles(Collection<String> roles) {
        Set<String> allRoles = profile.getRoles();
        List<String> pullValues = new ArrayList<>();

        for (String role : roles) {
            if (allRoles.remove(role)) {
                pullValues.add(role);
            }
        }

        updateHelper.pullAll("roles", pullValues);
    }

    public void setAttributes(Map<String, Object> attributes) {
        profile.setAttributes(attributes);
        updateHelper.set("attributes", attributes);
    }

    public void addAttributes(Map<String, Object> attributes) {
        for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
            String name = attribute.getKey();
            Object value = attribute.getValue();

            profile.getAttributes().put(name, value);
            updateHelper.set("attributes." + name, value);
        }
    }

    public void removeAttributes(Collection<String> attributeNames) {
        for (String attributeName : attributeNames) {
            profile.getAttributes().remove(attributeName);
            updateHelper.unset("attributes." + attributeName);
        }
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        profile.setFailedLoginAttempts(failedLoginAttempts);
        updateHelper.set("failedLoginAttempts", failedLoginAttempts);
    }

    public void setLastFailedLogin(Date lastFailedLogin) {
        profile.setLastFailedLogin(lastFailedLogin);
        updateHelper.set("lastFailedLogin", lastFailedLogin);
    }

    public void update() throws MongoDataException {
        updateHelper.executeUpdate(profile.getId().toString(), profileRepository);
    }
    
}
