package com.example.roomfit.config;

import com.example.roomfit.repository.MemberRepository;
import com.example.roomfit.security.LoginMember;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberRepository memberRepository;

	@Override
	public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
		return memberRepository.findByLoginId(loginId)
				.map(LoginMember::of)
				.orElseThrow(() -> new UsernameNotFoundException("로그인 정보가 올바르지 않습니다."));
	}
}
