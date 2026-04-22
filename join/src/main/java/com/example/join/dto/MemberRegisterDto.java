package com.example.join.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MemberRegisterDto {

	private String userId;
	private String password;
	private String name;
	private String email;
}
