package com.example.shoppingmall.dto;

public record AdminProductEditResponse(ProductFormDto form, ProductDetailDto detail) {

	public static AdminProductEditResponse of(ProductFormDto form, ProductDetailDto detail) {
		return new AdminProductEditResponse(form, detail);
	}
}
