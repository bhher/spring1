package com.example.shoppingmall.web.api;

import com.example.shoppingmall.dto.AdminProductEditResponse;
import com.example.shoppingmall.dto.ProductFormDto;
import com.example.shoppingmall.dto.ProductSummaryDto;
import com.example.shoppingmall.service.ProductService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class ApiAdminProductController {

	private final ProductService productService;

	@GetMapping
	public Page<ProductSummaryDto> list(
			@RequestParam(required = false) String keyword,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
		return productService.findProducts(keyword, pageable);
	}

	@GetMapping("/new")
	public ProductFormDto newForm() {
		return new ProductFormDto();
	}

	@GetMapping("/{id}")
	public AdminProductEditResponse editForm(@PathVariable Long id) {
		return AdminProductEditResponse.of(productService.getProductForm(id), productService.findDetail(id));
	}

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> create(
			@Valid @RequestPart("data") ProductFormDto form,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {
		var saved = productService.saveProduct(form, images);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
				"message", "상품을 등록했습니다.",
				"id", saved.getId()));
	}

	@PutMapping(path = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> update(
			@PathVariable Long id,
			@Valid @RequestPart("data") ProductFormDto form,
			@RequestPart(value = "images", required = false) List<MultipartFile> images) {
		productService.updateProduct(id, form, images);
		return ResponseEntity.ok(Map.of("message", "상품을 수정했습니다."));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		productService.deleteProduct(id);
	}
}
