package com.example.shoppingmall.dto;

import com.example.shoppingmall.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailDto(
		Long id,
		String orderNumber,
		BigDecimal totalAmount,
		OrderStatus status,
		LocalDateTime orderedAt,
		LocalDateTime paymentConfirmedAt,
		List<OrderLineDto> lines
) {
	public record OrderLineDto(
			Long productId,
			String productName,
			BigDecimal unitPrice,
			Integer quantity,
			BigDecimal lineTotal
	) {
	}
}
