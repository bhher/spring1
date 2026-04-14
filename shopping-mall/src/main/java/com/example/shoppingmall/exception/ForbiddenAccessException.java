package com.example.shoppingmall.exception;

/**
 * 권한 없음 (403).
 */
public class ForbiddenAccessException extends RuntimeException {

	public ForbiddenAccessException(String message) {
		super(message);
	}
}
