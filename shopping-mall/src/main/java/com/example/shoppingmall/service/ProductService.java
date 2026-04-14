package com.example.shoppingmall.service;

import com.example.shoppingmall.domain.Product;
import com.example.shoppingmall.domain.ProductImage;
import com.example.shoppingmall.dto.ProductDetailDto;
import com.example.shoppingmall.dto.ProductDetailDto.ProductImageDto;
import com.example.shoppingmall.dto.ProductFormDto;
import com.example.shoppingmall.dto.ProductSummaryDto;
import com.example.shoppingmall.exception.ResourceNotFoundException;
import com.example.shoppingmall.repository.ProductRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final FileStorageService fileStorageService;

	@Transactional(readOnly = true)
	public Page<ProductSummaryDto> findProducts(String keyword, Pageable pageable) {
		Page<Product> page = (keyword == null || keyword.isBlank())
				? productRepository.findAllWithImages(pageable)
				: productRepository.searchByName(keyword.trim(), pageable);
		return page.map(this::toSummary);
	}

	@Transactional(readOnly = true)
	public ProductDetailDto findDetail(Long id) {
		Product p = productRepository.findWithImagesById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
		return toDetail(p);
	}

	/** 관리자 수정 폼용: 기존 상품 정보를 DTO 로 채웁니다. */
	@Transactional(readOnly = true)
	public ProductFormDto getProductForm(Long id) {
		Product p = productRepository.findWithImagesById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
		ProductFormDto dto = new ProductFormDto();
		dto.setId(p.getId());
		dto.setName(p.getName());
		dto.setDescription(p.getDescription());
		dto.setPrice(p.getPrice());
		dto.setStockQuantity(p.getStockQuantity());
		return dto;
	}

	@Transactional
	public Product saveProduct(ProductFormDto form, List<MultipartFile> imageFiles) {
		Product product = Product.builder()
				.name(form.getName())
				.description(form.getDescription())
				.price(form.getPrice())
				.stockQuantity(form.getStockQuantity())
				.build();
		product = productRepository.save(product);
		attachImages(product, imageFiles);
		return productRepository.findWithImagesById(product.getId()).orElse(product);
	}

	@Transactional
	public Product updateProduct(Long id, ProductFormDto form, List<MultipartFile> newImages) {
		Product product = productRepository.findWithImagesById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
		product.setName(form.getName());
		product.setDescription(form.getDescription());
		product.setPrice(form.getPrice());
		product.setStockQuantity(form.getStockQuantity());
		attachImages(product, newImages);
		return productRepository.save(product);
	}

	@Transactional
	public void deleteProduct(Long id) {
		Product product = productRepository.findWithImagesById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
		for (ProductImage img : product.getImages()) {
			fileStorageService.deleteImageFiles(img);
		}
		productRepository.delete(product);
	}

	private void attachImages(Product product, List<MultipartFile> files) {
		if (files == null || files.isEmpty()) {
			return;
		}
		int start = product.getImages().stream()
				.mapToInt(ProductImage::getSortOrder)
				.max()
				.orElse(-1) + 1;
		int i = 0;
		for (MultipartFile file : files) {
			if (file == null || file.isEmpty()) {
				continue;
			}
			ProductImage image = fileStorageService.storeProductImage(file, product, start + (i++));
			product.addImage(image);
		}
	}

	private ProductSummaryDto toSummary(Product p) {
		String thumb = p.getImages().stream()
				.min(Comparator.comparing(ProductImage::getSortOrder))
				.map(ProductImage::getThumbnailUrlPath)
				.orElse("/images/no-image.svg");
		return new ProductSummaryDto(p.getId(), p.getName(), p.getPrice(), p.getStockQuantity(), thumb);
	}

	private ProductDetailDto toDetail(Product p) {
		List<ProductImageDto> imgs = p.getImages().stream()
				.sorted(Comparator.comparing(ProductImage::getSortOrder))
				.map(img -> new ProductImageDto(
						img.getId(),
						img.getOriginalFilename(),
						img.getUrlPath(),
						img.getThumbnailUrlPath()))
				.toList();
		return new ProductDetailDto(
				p.getId(),
				p.getName(),
				p.getDescription(),
				p.getPrice(),
				p.getStockQuantity(),
				p.getCreatedAt(),
				imgs);
	}
}
