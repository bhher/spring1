package com.example.shoppingmall.exception;

/**
 * 재고 부족, 주문 불가 등 비즈니스 규칙 위반.
 */
public class BusinessException extends RuntimeException {

	public BusinessException(String message) {
		super(message);
	}
}
