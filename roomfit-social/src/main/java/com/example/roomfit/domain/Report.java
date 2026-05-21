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
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Member reporter;

	@Column(nullable = false, length = 20)
	private String targetType;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false, length = 500)
	private String reason;

	@Enumerated(EnumType.STRING)
	@Builder.Default
	private ReportStatus status = ReportStatus.PENDING;

	@Column(length = 500)
	private String adminNote;

	@Builder.Default
	private LocalDateTime createdAt = LocalDateTime.now();
}
