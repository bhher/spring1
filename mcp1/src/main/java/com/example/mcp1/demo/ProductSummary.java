package com.example.mcp1.demo;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Structured Output (entity / BeanOutputConverter) 데모용 스키마.
 */
public record ProductSummary(
		@JsonPropertyDescription("상품명") String name,
		@JsonPropertyDescription("카테고리") String category,
		@JsonPropertyDescription("가격(원, 정수)") int priceKrw) {
}
