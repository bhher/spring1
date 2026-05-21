package com.example.boardloginimgsecurityoauth.security;

import com.example.boardloginimgsecurityoauth.domain.User;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 폼 로그인·소셜 로그인(OAuth2/OIDC) 공통 Principal.
 */
public class LoginUser implements UserDetails, OAuth2User, OidcUser {

    private final User user;
    private final Map<String, Object> attributes;
    private final String nameAttributeKey;
    private final OidcIdToken idToken;
    private final OidcUserInfo userInfo;

    public static LoginUser of(User user) {
        return new LoginUser(user, Collections.emptyMap(), "sub", null, null);
    }

    public static LoginUser ofOAuth(User user, Map<String, Object> attributes, String nameAttributeKey) {
        return new LoginUser(user, attributes, nameAttributeKey, null, null);
    }

    public static LoginUser ofOidc(User user, OidcIdToken idToken, OidcUserInfo userInfo) {
        Map<String, Object> claims = userInfo != null ? userInfo.getClaims() : Collections.emptyMap();
        return new LoginUser(user, claims, "sub", idToken, userInfo);
    }

    private LoginUser(
            User user,
            Map<String, Object> attributes,
            String nameAttributeKey,
            OidcIdToken idToken,
            OidcUserInfo userInfo) {
        this.user = user;
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.idToken = idToken;
        this.userInfo = userInfo;
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getDisplayName() {
        return user.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Map<String, Object> getClaims() {
        return userInfo != null ? userInfo.getClaims() : attributes;
    }

    @Override
    public OidcIdToken getIdToken() {
        return idToken;
    }

    @Override
    public OidcUserInfo getUserInfo() {
        return userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        if (attributes != null && !attributes.isEmpty()) {
            Object v = attributes.get(nameAttributeKey);
            if (v != null) {
                return String.valueOf(v);
            }
        }
        return user.getUsername();
    }
}
