package com.example.boardlogin.mapper.web.dto;

import lombok.Getter;

@Getter
public class UserProfileDto {

    private final String username;
    private final String name;

    public UserProfileDto(String username, String name) {
        this.username = username;
        this.name = name;
    }
}
