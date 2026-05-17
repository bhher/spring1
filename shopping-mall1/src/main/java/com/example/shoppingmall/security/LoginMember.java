package com.example.shoppingmall.security;

import com.example.shoppingmall.domain.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * 폼 로그인({@link Member})과 소셜 로그인(OAuth2) 모두에서 동일한 Principal 타입으로 사용합니다.
 */
@Getter
@RequiredArgsConstructor
public class LoginMember implements UserDetails, OAuth2User {

	private final Member member;
	private final Map<String, Object> attributes;
	private final String nameAttributeKey;

	public static LoginMember of(Member member) {
		return new LoginMember(member, Collections.emptyMap(), "email");
	}

	public static LoginMember ofOAuth(Member member, Map<String, Object> attributes, String nameAttributeKey) {
		return new LoginMember(member, attributes, nameAttributeKey);
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
		Object v = attributes.get(nameAttributeKey);
		if (v != null) {
			return String.valueOf(v);
		}
		return member.getUsername();
	}
}
