package com.example.shoppingmall.exception;

/**
 * 조회 결과가 없을 때 (404).
 */
public class ResourceNotFoundException extends RuntimeException {

	public ResourceNotFoundException(String message) {
		super(message);
	}
}
