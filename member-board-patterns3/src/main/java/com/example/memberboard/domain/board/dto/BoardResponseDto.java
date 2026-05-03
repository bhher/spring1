package com.example.memberboard.domain.board.dto;

import com.example.memberboard.domain.board.Board;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class BoardResponseDto {

	private final Long id;
	private final String title;
	private final String content;
	private final Long authorId;
	private final String authorNickname;
	private final LocalDateTime createdAt;
	private final LocalDateTime updatedAt;

	public BoardResponseDto(Board board) {
		this.id = board.getId();
		this.title = board.getTitle();
		this.content = board.getContent();
		this.authorId = board.getAuthor().getId();
		this.authorNickname = board.getAuthor().getNickname();
		this.createdAt = board.getCreatedAt();
		this.updatedAt = board.getUpdatedAt();
	}
}
