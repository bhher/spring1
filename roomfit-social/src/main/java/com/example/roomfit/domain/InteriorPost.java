package com.example.roomfit.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "interior_posts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class InteriorPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Member author;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private InteriorStyle style;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	private BigDecimal roomSize;
	private Integer budget;

	@Builder.Default
	private int viewCount = 0;

	@Builder.Default
	private int likeCount = 0;

	@Builder.Default
	private int commentCount = 0;

	@Builder.Default
	private boolean hasFurnitureTag = false;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private PostStatus status = PostStatus.VISIBLE;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();

	private LocalDateTime updatedAt;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	@OrderBy("sortOrder ASC")
	@Builder.Default
	private List<PostImage> images = new ArrayList<>();

	public void addImage(PostImage image) {
		images.add(image);
		image.setPost(this);
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	/** 목록/메인용 썸네일 (images 가 @EntityGraph 로 로드된 뒤 사용) */
	public String getThumbnailPath() {
		if (images == null || images.isEmpty()) {
			return "/images/no-image.svg";
		}
		String path = images.stream()
				.filter(PostImage::isThumbnail)
				.map(PostImage::getFilePath)
				.filter(p -> p != null && !p.isBlank())
				.findFirst()
				.orElseGet(() -> images.stream()
						.map(PostImage::getFilePath)
						.filter(p -> p != null && !p.isBlank())
						.findFirst()
						.orElse(null));
		if (path == null) {
			return "/images/no-image.svg";
		}
		return path;
	}
}
