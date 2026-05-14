package com.example.shoppingmall.dto;

import com.example.shoppingmall.domain.Member;

public record MemberMeDto(String email, String name, String role) {

	public static MemberMeDto from(Member member) {
		return new MemberMeDto(member.getEmail(), member.getName(), member.getRole().name());
	}
}
