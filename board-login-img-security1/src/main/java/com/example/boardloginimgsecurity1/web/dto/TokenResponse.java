package com.example.boardloginimgsecurity1.web.dto;

public class TokenResponse {

    private final String accessToken;
    private final String tokenType;
    private final String username;
    private final String displayName;

    public TokenResponse(String accessToken, String tokenType, String username, String displayName) {
        this.accessToken = accessToken;
        this.tokenType = tokenType;
        this.username = username;
        this.displayName = displayName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public String getUsername() {
        return username;
    }

    public String getDisplayName() {
        return displayName;
    }
}
