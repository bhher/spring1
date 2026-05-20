package com.example.roomfit.service;

import com.example.roomfit.domain.Product;
import com.example.roomfit.dto.ProductFormDto;
import com.example.roomfit.exception.ResourceNotFoundException;
import com.example.roomfit.repository.ProductRepository;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductAdminService {

	private final ProductRepository productRepository;
	private final FileStorageService fileStorageService;

	public Page<Product> findProducts(String keyword, Pageable pageable) {
		if (keyword == null || keyword.isBlank()) {
			return productRepository.findAllByOrderByIdDesc(pageable);
		}
		return productRepository.findByNameContainingIgnoreCaseOrderByIdDesc(keyword.trim(), pageable);
	}

	public Product getProduct(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
	}

	public ProductFormDto getForm(Long id) {
		Product product = getProduct(id);
		ProductFormDto dto = new ProductFormDto();
		dto.setId(product.getId());
		dto.setName(product.getName());
		dto.setPrice(product.getPrice());
		dto.setStock(product.getStock());
		dto.setStyleTag(product.getStyleTag());
		dto.setOnSale(product.isOnSale());
		return dto;
	}

	@Transactional
	public Long create(ProductFormDto form, MultipartFile image) throws IOException {
		String imagePath = resolveImagePath(image, null);
		Product product = Product.builder()
				.name(form.getName())
				.price(form.getPrice())
				.stock(form.getStock())
				.styleTag(form.getStyleTag())
				.onSale(form.isOnSale())
				.imagePath(imagePath)
				.build();
		return productRepository.save(product).getId();
	}

	@Transactional
	public void update(Long id, ProductFormDto form, MultipartFile image) throws IOException {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
		product.setName(form.getName());
		product.setPrice(form.getPrice());
		product.setStock(form.getStock());
		product.setStyleTag(form.getStyleTag());
		product.setOnSale(form.isOnSale());
		String imagePath = resolveImagePath(image, product.getImagePath());
		if (imagePath != null) {
			product.setImagePath(imagePath);
		}
	}

	/** 판매 중지(소프트 삭제). 장바구니·리뷰 등 FK 때문에 물리 삭제는 하지 않습니다. */
	@Transactional
	public void discontinue(Long id) {
		Product product = productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
		product.setOnSale(false);
	}

	private String resolveImagePath(MultipartFile image, String currentPath) throws IOException {
		String uploaded = fileStorageService.storeProductImage(image);
		if (uploaded != null) {
			return uploaded;
		}
		return currentPath;
	}
}
