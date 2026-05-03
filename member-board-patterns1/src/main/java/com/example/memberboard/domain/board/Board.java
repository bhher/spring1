package com.example.memberboard.domain.board;

import com.example.memberboard.domain.user.User;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 2단계(생성자): 신규 글은 {@code new Board(...)} , 수정은 도메인 메서드 {@link #edit} 로 표현합니다.
 */
@Entity
@Table(name = "boards")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Access(AccessType.FIELD)
public class Board {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "author_id")
	private User author;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;

	public Board(String title, String content, User author, LocalDateTime createdAt, LocalDateTime updatedAt) {
		this.title = title;
		this.content = content;
		this.author = author;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}

	public void edit(String title, String content, LocalDateTime updatedAt) {
		this.title = title;
		this.content = content;
		this.updatedAt = updatedAt;
	}
}
