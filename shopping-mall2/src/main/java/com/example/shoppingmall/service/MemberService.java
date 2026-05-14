package com.example.shoppingmall.service;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.domain.Role;
import com.example.shoppingmall.dto.MemberRegisterDto;
import com.example.shoppingmall.exception.BusinessException;
import com.example.shoppingmall.repository.MemberRepository;
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
}
