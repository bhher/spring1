package com.example.shoppingmall.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 상품 이미지 (원본 + 썸네일 파일명은 서비스에서 UUID 로 저장).
 */
@Entity
@Table(name = "product_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String originalFilename;

	/** 저장 파일명 (UUID 포함) */
	@Column(nullable = false, length = 255)
	private String storedFilename;

	/** 썸네일 파일명 (보통 s_ 접두어) */
	@Column(length = 255)
	private String thumbnailFilename;

	/** 디렉터리 경로 (끝에 구분자 포함) */
	@Column(nullable = false, length = 500)
	private String directoryPath;

	@Column(nullable = false)
	private Integer sortOrder;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "product_id")
	private Product product;

	public String getUrlPath() {
		return "/uploads/products/" + storedFilename;
	}

	public String getThumbnailUrlPath() {
		if (thumbnailFilename == null || thumbnailFilename.isBlank()) {
			return getUrlPath();
		}
		return "/uploads/products/" + thumbnailFilename;
	}
}
