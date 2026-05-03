package com.example.memberboard.domain.board.dto;

import com.example.memberboard.domain.board.Board;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 3단계(빌더): {@link #from(Board)} 에서 Builder 로 응답 DTO를 만듭니다.
 */
@Getter
@Builder
public class BoardResponseDto {

	private final Long id;
	private final String title;
	private final String content;
	private final Long authorId;
	private final String authorNickname;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public static BoardResponseDto from(Board board) {
		return BoardResponseDto.builder()
				.id(board.getId())
				.title(board.getTitle())
				.content(board.getContent())
				.authorId(board.getAuthor().getId())
				.authorNickname(board.getAuthor().getNickname())
				.createdAt(board.getCreatedAt())
				.updatedAt(board.getUpdatedAt())
				.build();
	}
}
