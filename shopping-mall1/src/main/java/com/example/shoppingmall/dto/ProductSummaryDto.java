package com.example.shoppingmall.dto;

import java.math.BigDecimal;

/** 목록/검색 결과용 */
public record ProductSummaryDto(
		Long id,
		String name,
		BigDecimal price,
		Integer stockQuantity,
		String thumbnailUrl
) {
}
