package com.example.roomfit.service;

import com.example.roomfit.domain.Gender;
import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.MemberStatus;
import com.example.roomfit.domain.Role;
import com.example.roomfit.dto.MemberRegisterDto;
import com.example.roomfit.exception.BusinessException;
import com.example.roomfit.exception.ResourceNotFoundException;
import com.example.roomfit.repository.MemberRepository;
import com.example.roomfit.security.OAuthUserProfile;
import java.time.LocalDateTime;
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
	public void register(MemberRegisterDto dto) {
		if (!dto.getPassword().equals(dto.getPasswordConfirm())) {
			throw new BusinessException("비밀번호가 일치하지 않습니다.");
		}
		if (memberRepository.existsByLoginId(dto.getLoginId())) {
			throw new BusinessException("이미 사용 중인 아이디입니다.");
		}
		if (memberRepository.existsByEmail(dto.getEmail())) {
			throw new BusinessException("이미 사용 중인 이메일입니다.");
		}
		Member member = Member.builder()
				.loginId(dto.getLoginId())
				.password(passwordEncoder.encode(dto.getPassword()))
				.name(dto.getName())
				.nickname(dto.getNickname())
				.email(dto.getEmail())
				.phone(dto.getPhone() != null ? dto.getPhone() : "")
				.gender(dto.getGender())
				.role(Role.USER)
				.status(MemberStatus.ACTIVE)
				.build();
		memberRepository.save(member);
	}

	/**
	 * 소셜 로그인 콜백: (provider + subject) 조회 → 없으면 동일 이메일 계정 연동 → 없으면 신규 가입.
	 */
	@Transactional
	public Member registerOrUpdateOAuthMember(String registrationId, OAuthUserProfile profile) {
		return memberRepository
				.findByOauthProviderAndOauthProviderSubject(registrationId, profile.getProviderUserId())
				.map(existing -> updateOAuthProfile(existing, profile))
				.orElseGet(() -> memberRepository.findByEmail(profile.getEmail())
						.map(existing -> linkOAuthAccount(existing, registrationId, profile))
						.orElseGet(() -> createOAuthMember(registrationId, profile)));
	}

	private Member updateOAuthProfile(Member member, OAuthUserProfile profile) {
		member.setName(profile.getName());
		member.setNickname(profile.getNickname());
		member.setUpdatedAt(LocalDateTime.now());
		return memberRepository.save(member);
	}

	private Member linkOAuthAccount(Member member, String registrationId, OAuthUserProfile profile) {
		member.setOauthProvider(registrationId);
		member.setOauthProviderSubject(profile.getProviderUserId());
		member.setName(profile.getName());
		member.setNickname(profile.getNickname());
		member.setUpdatedAt(LocalDateTime.now());
		return memberRepository.save(member);
	}

	private Member createOAuthMember(String registrationId, OAuthUserProfile profile) {
		Member member = Member.builder()
				.loginId(generateUniqueLoginId(registrationId, profile.getProviderUserId()))
				.password(passwordEncoder.encode(UUID.randomUUID().toString()))
				.name(profile.getName())
				.nickname(profile.getNickname())
				.email(profile.getEmail())
				.phone("")
				.role(Role.USER)
				.status(MemberStatus.ACTIVE)
				.oauthProvider(registrationId)
				.oauthProviderSubject(profile.getProviderUserId())
				.build();
		return memberRepository.save(member);
	}

	private String generateUniqueLoginId(String registrationId, String providerUserId) {
		String base = registrationId + "_" + providerUserId;
		if (base.length() > 45) {
			base = base.substring(0, 45);
		}
		String loginId = base;
		int suffix = 1;
		while (memberRepository.existsByLoginId(loginId)) {
			loginId = base + "_" + suffix++;
			if (loginId.length() > 50) {
				loginId = registrationId + "_" + suffix;
			}
		}
		return loginId;
	}

	public Member findByLoginId(String loginId) {
		return memberRepository.findByLoginId(loginId)
				.orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
	}

	public Member findById(Long id) {
		return memberRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다."));
	}

	public String findLoginId(String name, String email) {
		return memberRepository.findByNameAndEmail(name, email)
				.map(Member::getLoginId)
				.orElseThrow(() -> new ResourceNotFoundException("일치하는 회원 정보가 없습니다."));
	}

	@Transactional
	public void resetPassword(String loginId, String email, String newPassword) {
		Member member = memberRepository.findByLoginId(loginId)
				.filter(m -> m.getEmail().equals(email))
				.orElseThrow(() -> new ResourceNotFoundException("일치하는 회원 정보가 없습니다."));
		member.setPassword(passwordEncoder.encode(newPassword));
	}

	@Transactional
	public void updateMember(Long memberId, String nickname, String phone, Gender gender) {
		Member member = findById(memberId);
		member.setNickname(nickname);
		member.setPhone(phone != null ? phone : "");
		member.setGender(gender);
		member.setUpdatedAt(LocalDateTime.now());
	}

	@Transactional
	public void withdraw(Long memberId) {
		Member member = findById(memberId);
		member.setStatus(MemberStatus.WITHDRAWN);
		member.setWithdrawnAt(LocalDateTime.now());
	}
}
