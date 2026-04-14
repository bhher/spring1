package com.example.shoppingmall.dto;

import java.math.BigDecimal;

public record CartLineDto(
		Long cartItemId,
		Long productId,
		String productName,
		BigDecimal unitPrice,
		Integer quantity,
		BigDecimal lineAmount,
		String thumbnailUrl
) {
}
