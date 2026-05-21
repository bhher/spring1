package com.example.roomfit.dto;

import com.example.roomfit.domain.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRegisterDto {

	@NotBlank(message = "아이디를 입력하세요.")
	@Size(min = 4, max = 50, message = "아이디는 4~50자입니다.")
	private String loginId;

	@NotBlank(message = "비밀번호를 입력하세요.")
	@Size(min = 4, max = 100)
	private String password;

	@NotBlank
	private String passwordConfirm;

	@NotBlank
	private String name;

	@NotBlank
	private String nickname;

	@NotBlank
	@Email
	private String email;

	private String phone;
	private Gender gender;
}
