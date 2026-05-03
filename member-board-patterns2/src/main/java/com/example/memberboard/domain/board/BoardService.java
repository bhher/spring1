package com.example.memberboard.domain.board;

import com.example.memberboard.domain.board.dto.BoardResponseDto;
import com.example.memberboard.domain.board.dto.BoardWriteDto;
import com.example.memberboard.domain.user.User;
import com.example.memberboard.domain.user.UserService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

	private final BoardRepository boardRepository;
	private final UserService userService;

	public List<BoardResponseDto> findAll() {
		return boardRepository.findAllWithAuthor().stream()
				.map(BoardResponseDto::from)
				.toList();
	}

	public BoardResponseDto findOne(Long boardId) {
		Board board = boardRepository.findByIdWithAuthor(boardId)
				.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
		return BoardResponseDto.from(board);
	}

	@Transactional
	public Long create(Long loginUserId, BoardWriteDto dto) {
		User author = userService.getEntity(loginUserId);
		LocalDateTime now = LocalDateTime.now();

		Board board = Board.builder()
				.title(dto.getTitle())
				.content(dto.getContent())
				.author(author)
				.createdAt(now)
				.updatedAt(now)
				.build();

		return boardRepository.save(board).getId();
	}

	@Transactional
	public void update(Long boardId, Long loginUserId, BoardWriteDto dto) {
		Board board = boardRepository.findByIdWithAuthor(boardId)
				.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
		if (!board.getAuthor().getId().equals(loginUserId)) {
			throw new IllegalStateException("작성자만 수정할 수 있습니다.");
		}
		board.edit(dto.getTitle(), dto.getContent(), LocalDateTime.now());
	}

	@Transactional
	public void delete(Long boardId, Long loginUserId) {
		Board board = boardRepository.findByIdWithAuthor(boardId)
				.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
		if (!board.getAuthor().getId().equals(loginUserId)) {
			throw new IllegalStateException("작성자만 삭제할 수 있습니다.");
		}
		boardRepository.delete(board);
	}
}
