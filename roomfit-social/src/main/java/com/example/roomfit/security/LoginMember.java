package com.example.roomfit.security;

import com.example.roomfit.domain.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 폼 로그인·소셜 로그인 모두에서 Security Principal 로 사용합니다.
 * 컨트롤러에서는 {@code @AuthenticationPrincipal(expression = "member") Member member} 로 주입합니다.
 */
@Getter
@RequiredArgsConstructor
public class LoginMember implements UserDetails, OAuth2User {

	private final Member member;
	private final Map<String, Object> attributes;
	private final String nameAttributeKey;

	public static LoginMember of(Member member) {
		return new LoginMember(member, Collections.emptyMap(), "loginId");
	}

	public static LoginMember ofOAuth(Member member, Map<String, Object> attributes, String nameAttributeKey) {
		return new LoginMember(member, attributes, nameAttributeKey);
	}

	public String getNickname() {
		return member.getNickname();
	}

	@Override
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return member.getAuthorities();
	}

	@Override
	public String getPassword() {
		return member.getPassword();
	}

	@Override
	public String getUsername() {
		return member.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return member.isAccountNonExpired();
	}

	@Override
	public boolean isAccountNonLocked() {
		return member.isAccountNonLocked();
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return member.isCredentialsNonExpired();
	}

	@Override
	public boolean isEnabled() {
		return member.isEnabled();
	}

	@Override
	public String getName() {
		if (attributes != null && !attributes.isEmpty()) {
			Object v = attributes.get(nameAttributeKey);
			if (v != null) {
				return String.valueOf(v);
			}
		}
		return member.getLoginId();
	}
}
