package com.example.boardloginimgsecurityoauth.security;

public class OAuthUserProfile {

    private final String providerUserId;
    private final String email;
    private final String name;

    public OAuthUserProfile(String providerUserId, String email, String name) {
        this.providerUserId = providerUserId;
        this.email = email;
        this.name = name;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
