package com.example.memberboard.domain.board;

import com.example.memberboard.domain.board.dto.BoardResponseDto;
import com.example.memberboard.domain.board.dto.BoardWriteDto;
import com.example.memberboard.domain.user.User;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class BoardMapper {

	public Board toNewEntity(BoardWriteDto dto, User author, LocalDateTime at) {
		return new Board(dto.getTitle(), dto.getContent(), author, at, at);
	}

	public BoardResponseDto toResponseDto(Board board) {
		return new BoardResponseDto(board);
	}

	public void apply(Board board, BoardWriteDto dto, LocalDateTime updatedAt) {
		board.edit(dto.getTitle(), dto.getContent(), updatedAt);
	}
}
