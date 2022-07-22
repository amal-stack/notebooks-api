package com.amalstack.api.notebooks.dto;

import com.amalstack.api.notebooks.model.AppUser;

import java.io.Serializable;

public record AppUserInfoDto(
        Long id,
        String username,
        String name) implements Serializable {

    public static AppUserInfoDto fromAppUser(AppUser user) {
        return new AppUserInfoDto(user.getId(), user.getUsername(), user.getName());
    }
}

