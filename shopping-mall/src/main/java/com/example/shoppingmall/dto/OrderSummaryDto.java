package com.example.shoppingmall.dto;

import com.example.shoppingmall.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrderSummaryDto(
		Long id,
		String orderNumber,
		BigDecimal totalAmount,
		OrderStatus status,
		LocalDateTime orderedAt
) {
}
