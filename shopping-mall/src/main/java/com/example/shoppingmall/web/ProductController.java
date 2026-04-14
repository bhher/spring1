package com.example.shoppingmall.web;

import com.example.shoppingmall.dto.ProductDetailDto;
import com.example.shoppingmall.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 공개 상품 목록·상세·검색.
 */
@Controller
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@GetMapping("/products")
	public String list(
			@RequestParam(required = false) String keyword,
			@PageableDefault(size = 12, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			Model model) {
		model.addAttribute("page", productService.findProducts(keyword, pageable));
		model.addAttribute("keyword", keyword == null ? "" : keyword);
		return "products/list";
	}

	@GetMapping("/products/{id}")
	public String detail(@PathVariable Long id, Model model) {
		ProductDetailDto dto = productService.findDetail(id);
		model.addAttribute("product", dto);
		return "products/detail";
	}
}
