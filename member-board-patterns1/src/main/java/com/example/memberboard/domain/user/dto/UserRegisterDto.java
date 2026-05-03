package com.example.memberboard.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDto {

	@NotBlank
	@Email
	@Size(max = 120)
	private String email;

	@NotBlank
	@Size(min = 4, max = 72, message = "비밀번호는 4~72자")
	private String password;

	@NotBlank
	@Size(max = 50)
	private String nickname;
}
