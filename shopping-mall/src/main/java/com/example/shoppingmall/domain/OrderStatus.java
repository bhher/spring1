package com.example.shoppingmall.domain;

/**
 * 주문 상태 (모의 결제이므로 결제 대기 없이 주문완료로 시작).
 */
public enum OrderStatus {
	/** 주문 완료 (모의 결제 성공으로 간주) */
	ORDERED,
	/** 취소 */
	CANCELLED
}
