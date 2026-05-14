package com.example.shoppingmall.security;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.service.MemberService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final MemberService memberService;

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2User oauth2User = super.loadUser(userRequest);
		String registrationId = userRequest.getClientRegistration().getRegistrationId();
		OAuthUserProfile profile = extractProfile(registrationId, oauth2User.getAttributes());
		Member saved = memberService.registerOrUpdateOAuthMember(registrationId, profile);
		String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
				.getUserNameAttributeName();
		return LoginMember.ofOAuth(saved, oauth2User.getAttributes(), nameAttributeKey);
	}

	private OAuthUserProfile extractProfile(String registrationId, Map<String, Object> attrs) {
		return switch (registrationId) {
			case "google" -> new OAuthUserProfile(
					String.valueOf(attrs.get("sub")),
					(String) attrs.get("email"),
					firstNonBlank((String) attrs.get("name"), (String) attrs.get("email")));
			case "naver" -> {
				@SuppressWarnings("unchecked")
				Map<String, Object> response = (Map<String, Object>) attrs.get("response");
				if (response == null) {
					throw new OAuth2AuthenticationException(new OAuth2Error("invalid_response"), "naver response missing");
				}
				yield new OAuthUserProfile(
						String.valueOf(response.get("id")),
						(String) response.get("email"),
						firstNonBlank((String) response.get("name"), (String) response.get("email")));
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

	private static String firstNonBlank(String a, String b) {
		if (a != null && !a.isBlank()) {
			return a;
		}
		return b != null ? b : "user";
	}
}
