package com.example.join1.dto;

/**
 * 가입 완료 화면용(비밀번호 제외).
 */
public class MemberSummaryDto {

	private String userId;
	private String name;
	private String email;

	public MemberSummaryDto() {
	}

	public MemberSummaryDto(String userId, String name, String email) {
		this.userId = userId;
		this.name = name;
		this.email = email;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
