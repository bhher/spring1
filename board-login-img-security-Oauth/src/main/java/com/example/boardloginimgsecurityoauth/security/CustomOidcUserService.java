package com.example.boardloginimgsecurityoauth.security;

import com.example.boardloginimgsecurityoauth.domain.User;
import com.example.boardloginimgsecurityoauth.service.UserService;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * Google 등 openid scope 사용 시 OIDC principal 을 {@link LoginUser} 로 통일합니다.
 */
@Service
public class CustomOidcUserService extends OidcUserService {

    private final UserService userService;

    public CustomOidcUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> claims = oidcUser.getUserInfo().getClaims();
        OAuthUserProfile profile = new OAuthUserProfile(
                String.valueOf(claims.get("sub")),
                (String) claims.get("email"),
                firstNonBlank((String) claims.get("name"), (String) claims.get("email"), "구글사용자"));
        User saved = userService.registerOrUpdateOAuthUser(registrationId, profile);
        return LoginUser.ofOidc(saved, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private static String firstNonBlank(String a, String b, String fallback) {
        if (a != null && !a.isBlank()) {
            return a;
        }
        if (b != null && !b.isBlank()) {
            return b;
        }
        return fallback;
    }
}
