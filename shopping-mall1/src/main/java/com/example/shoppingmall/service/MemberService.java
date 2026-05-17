package com.example.shoppingmall.service;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.domain.Role;
import com.example.shoppingmall.dto.MemberRegisterDto;
import com.example.shoppingmall.exception.BusinessException;
import com.example.shoppingmall.repository.MemberRepository;
import com.example.shoppingmall.security.OAuthUserProfile;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public Member register(MemberRegisterDto dto) {
		if (memberRepository.existsByEmail(dto.getEmail())) {
			throw new BusinessException("이미 가입된 이메일입니다.");
		}
		Member member = Member.builder()
				.email(dto.getEmail())
				.password(passwordEncoder.encode(dto.getPassword()))
				.name(dto.getName())
				.role(Role.USER)
				.build();
		return memberRepository.save(member);
	}

	/**
	 * 소셜 로그인 콜백에서 호출합니다. (provider + subject)로 기존 회원을 찾거나, 이메일로 연동·신규 가입합니다.
	 */
	@Transactional
	public Member registerOrUpdateOAuthMember(String registrationId, OAuthUserProfile profile) {
		return memberRepository
				.findByOauthProviderAndOauthProviderSubject(registrationId, profile.getProviderUserId())
				.map(existing -> {
					existing.setName(profile.getName());
					return memberRepository.save(existing);
				})
				.orElseGet(() -> memberRepository.findByEmail(profile.getEmail()).map(existing -> {
					existing.setOauthProvider(registrationId);
					existing.setOauthProviderSubject(profile.getProviderUserId());
					existing.setName(profile.getName());
					return memberRepository.save(existing);
				}).orElseGet(() -> {
					Member created = Member.builder()
							.email(profile.getEmail())
							.password(passwordEncoder.encode(UUID.randomUUID().toString()))
							.name(profile.getName())
							.role(Role.USER)
							.oauthProvider(registrationId)
							.oauthProviderSubject(profile.getProviderUserId())
							.build();
					return memberRepository.save(created);
				}));
	}
}
