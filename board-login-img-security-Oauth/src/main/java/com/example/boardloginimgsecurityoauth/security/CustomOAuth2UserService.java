package com.example.boardloginimgsecurityoauth.security;

import com.example.boardloginimgsecurityoauth.domain.User;
import com.example.boardloginimgsecurityoauth.service.UserService;
import java.util.Map;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    public CustomOAuth2UserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuthUserProfile profile = extractProfile(registrationId, oauth2User.getAttributes());
        User saved = userService.registerOrUpdateOAuthUser(registrationId, profile);
        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        return LoginUser.ofOAuth(saved, oauth2User.getAttributes(), nameAttributeKey);
    }

    private OAuthUserProfile extractProfile(String registrationId, Map<String, Object> attrs) {
        return switch (registrationId) {
            case "google" -> new OAuthUserProfile(
                    String.valueOf(attrs.get("sub")),
                    (String) attrs.get("email"),
                    firstNonBlank((String) attrs.get("name"), (String) attrs.get("email"), "구글사용자"));
            case "naver" -> {
                @SuppressWarnings("unchecked")
                Map<String, Object> response = (Map<String, Object>) attrs.get("response");
                if (response == null) {
                    throw new OAuth2AuthenticationException(new OAuth2Error("invalid_response"), "naver response missing");
                }
                String name = firstNonBlank((String) response.get("name"), (String) response.get("email"), "네이버사용자");
                yield new OAuthUserProfile(
                        String.valueOf(response.get("id")),
                        (String) response.get("email"),
                        name);
            }
            case "kakao" -> {
                String id = String.valueOf(attrs.get("id"));
                @SuppressWarnings("unchecked")
                Map<String, Object> kakaoAccount = (Map<String, Object>) attrs.get("kakao_account");
                String email = null;
                String nickname = "카카오사용자";
                if (kakaoAccount != null) {
                    email = (String) kakaoAccount.get("email");
                    @SuppressWarnings("unchecked")
                    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                    if (profile != null && profile.get("nickname") != null) {
                        nickname = String.valueOf(profile.get("nickname"));
                    }
                }
                if (email == null || email.isBlank()) {
                    email = "kakao_" + id + "@users.noreply.kakao";
                }
                yield new OAuthUserProfile(id, email, nickname);
            }
            default -> throw new OAuth2AuthenticationException(
                    new OAuth2Error("unsupported_provider"), "unsupported registration: " + registrationId);
        };
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
