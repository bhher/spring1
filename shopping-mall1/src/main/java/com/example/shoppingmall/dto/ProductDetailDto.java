package com.example.shoppingmall.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ProductDetailDto(
		Long id,
		String name,
		String description,
		BigDecimal price,
		Integer stockQuantity,
		LocalDateTime createdAt,
		List<ProductImageDto> images
) {
	public record ProductImageDto(Long id, String originalFilename, String url, String thumbnailUrl) {
	}
}
