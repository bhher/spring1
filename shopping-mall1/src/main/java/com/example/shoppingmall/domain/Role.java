package com.example.shoppingmall.domain;

/**
 * 회원 권한. Spring Security 에서는 ROLE_ 접두어가 붙은 권한으로 매핑됩니다 (ROLE_USER, ROLE_ADMIN).
 */
public enum Role {
	USER,
	ADMIN
}
