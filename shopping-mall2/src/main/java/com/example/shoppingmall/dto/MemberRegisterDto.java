package com.example.shoppingmall.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterDto {

	@NotBlank
	@Email
	@Size(max = 120)
	private String email;

	@NotBlank
	@Size(min = 4, max = 100)
	private String password;

	@NotBlank
	@Size(max = 50)
	private String name;
}
