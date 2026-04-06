package com.example.join1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SignupForm {

	@NotBlank(message = "아이디를 입력하세요.")
	@Size(min = 4, max = 20, message = "아이디는 4~20자입니다.")
	private String username;

	@NotBlank(message = "비밀번호를 입력하세요.")
	@Size(min = 6, max = 100, message = "비밀번호는 6자 이상입니다.")
	private String password;

	@NotBlank(message = "비밀번호 확인을 입력하세요.")
	private String passwordConfirm;

	@Email(message = "올바른 이메일 형식이 아닙니다.")
	@NotBlank(message = "이메일을 입력하세요.")
	private String email;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPasswordConfirm() {
		return passwordConfirm;
	}

	public void setPasswordConfirm(String passwordConfirm) {
		this.passwordConfirm = passwordConfirm;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
