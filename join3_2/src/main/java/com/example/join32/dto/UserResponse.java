package com.example.join32.dto;

import com.example.join32.domain.User;

public record UserResponse(Long id, String username, String name) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getName());
    }
}
