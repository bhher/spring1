package com.example.shoppingmall.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

/**
 * 관리자 상품 등록/수정 폼.
 */
@Getter
@Setter
public class ProductFormDto {

	private Long id;

	@NotBlank
	private String name;

	@NotBlank
	private String description;

	@NotNull
	private BigDecimal price;

	@NotNull
	@Min(0)
	private Integer stockQuantity;
}
