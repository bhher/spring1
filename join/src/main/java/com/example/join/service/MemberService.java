package com.example.join.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.join.dto.MemberRegisterDto;
import com.example.join.entity.Member;
import com.example.join.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional
	public Member register(MemberRegisterDto dto) {
		Member member = new Member();
		member.setUserId(dto.getUserId());
		member.setPassword(dto.getPassword());
		member.setName(dto.getName());
		member.setEmail(dto.getEmail());
		return memberRepository.save(member);
	}
}
