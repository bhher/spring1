package board01.board.service;

import board01.board.dto.BoardEditDto;
import board01.board.dto.BoardEditFormDto;
import board01.board.dto.BoardMapper;
import board01.board.dto.BoardResponseDto;
import board01.board.dto.BoardWriteDto;
import board01.board.entity.Board;
import board01.board.repository.BoardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardService {

	private final BoardRepository boardRepository;

	public BoardService(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	public Board saveFromWrite(BoardWriteDto dto) {
		return boardRepository.save(BoardMapper.toEntity(dto));
	}

	public List<BoardResponseDto> findAllDtos() {
		return boardRepository.findAll().stream()
				.map(BoardMapper::toResponseDto)
				.toList();
	}

	public Optional<BoardResponseDto> findResponseById(Long id) {
		return boardRepository.findById(id).map(BoardMapper::toResponseDto);
	}

	public Optional<BoardEditFormDto> findEditFormById(Long id) {
		return boardRepository.findById(id).map(BoardMapper::toEditFormDto);
	}

	public void update(Long id, BoardEditDto dto) {
		Board existing = boardRepository.findById(id).orElseThrow();
		existing.setTitle(dto.getTitle());
		existing.setContent(dto.getContent());
		boardRepository.save(existing);
	}

	public void deleteById(Long id) {
		boardRepository.deleteById(id);
	}
}
