package com.example.shoppingmall.web.api;

import com.example.shoppingmall.dto.ProductDetailDto;
import com.example.shoppingmall.dto.ProductSummaryDto;
import com.example.shoppingmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ApiProductController {

	private final ProductService productService;

	@GetMapping
	public Page<ProductSummaryDto> list(
			@RequestParam(required = false) String keyword,
			@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		return productService.findProducts(keyword, pageable);
	}

	@GetMapping("/{id}")
	public ProductDetailDto detail(@PathVariable Long id) {
		return productService.findDetail(id);
	}
}
