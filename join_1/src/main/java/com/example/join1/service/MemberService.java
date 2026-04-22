package com.example.join1.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.join1.dto.MemberRegisterDto;
import com.example.join1.dto.MemberSummaryDto;
import com.example.join1.entity.Member;
import com.example.join1.repository.MemberRepository;

@Service
public class MemberService {

	private final MemberRepository memberRepository;

	public MemberService(MemberRepository memberRepository) {
		this.memberRepository = memberRepository;
	}

	@Transactional
	public MemberSummaryDto register(MemberRegisterDto dto) {
		Member member = new Member();
		member.setUserId(dto.getUserId());
		member.setPassword(dto.getPassword());
		member.setName(dto.getName());
		member.setEmail(dto.getEmail());
		Member saved = memberRepository.save(member);
		return new MemberSummaryDto(saved.getUserId(), saved.getName(), saved.getEmail());
	}
}
