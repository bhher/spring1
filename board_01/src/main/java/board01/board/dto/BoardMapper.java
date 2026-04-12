package board01.board.dto;

import board01.board.entity.Board;

public final class BoardMapper {

	private BoardMapper() {
	}

	public static Board toEntity(BoardWriteDto dto) {
		Board board = new Board();
		board.setTitle(dto.getTitle());
		board.setWriter(dto.getWriter());
		board.setContent(dto.getContent());
		return board;
	}

	public static BoardResponseDto toResponseDto(Board board) {
		return new BoardResponseDto(
				board.getId(),
				board.getTitle(),
				board.getWriter(),
				board.getContent());
	}

	public static BoardEditFormDto toEditFormDto(Board board) {
		return new BoardEditFormDto(board.getId(), board.getTitle(), board.getContent());
	}
}
