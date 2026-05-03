package com.example.memberboard.domain.board.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardResponseDto {

	private Long id;
	private String title;
	private String content;
	private Long authorId;
	private String authorNickname;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
