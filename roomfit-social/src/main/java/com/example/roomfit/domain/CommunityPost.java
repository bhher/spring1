package com.example.roomfit.domain;

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
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "community_posts")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CommunityPost {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CommunityBoardType boardType;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Member author;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Builder.Default
	private int viewCount = 0;

	@Builder.Default
	private boolean answered = false;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private PostStatus status = PostStatus.VISIBLE;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
}
