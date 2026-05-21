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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class UserProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, unique = true)
	private Member member;

	@Column(nullable = false, precision = 4, scale = 1)
	private BigDecimal roomSize;

	@Column(nullable = false)
	private Integer budget;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private InteriorStyle preferredStyle;

	@Column(length = 50)
	private String lifestyle;

	@Builder.Default
	@Column(nullable = false)
	private boolean hasFurniture = false;

	@Column(length = 20)
	private String sleepPattern;

	@Builder.Default
	private LocalDateTime updatedAt = LocalDateTime.now();
}
