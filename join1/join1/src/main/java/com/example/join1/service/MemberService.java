package com.example.join1.service;

import com.example.join1.entity.Member;
import com.example.join1.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
		this.memberRepository = memberRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public void register(String username, String rawPassword, String email) {
		if (memberRepository.existsByUsername(username)) {
			throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
		}
		Member member = new Member();
		member.setUsername(username);
		member.setPassword(passwordEncoder.encode(rawPassword));
		member.setEmail(email);
		memberRepository.save(member);
	}
}
