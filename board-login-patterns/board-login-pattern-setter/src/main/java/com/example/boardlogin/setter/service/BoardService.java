package com.example.boardlogin.setter.service;

import com.example.boardlogin.setter.domain.Board;
import com.example.boardlogin.setter.domain.User;
import com.example.boardlogin.setter.repository.BoardRepository;
import com.example.boardlogin.setter.web.dto.BoardForm;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Setter 방식: 게시글도 생성 후 setter로 필드를 채웁니다.
 */
@Service
public class BoardService {

    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional(readOnly = true)
    public List<Board> findAll() {
        return boardRepository.findAllWithAuthor();
    }

    @Transactional(readOnly = true)
    public Board findById(Long id) {
        return boardRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
    }

    @Transactional
    public Board create(BoardForm form, User author) {
        Board board = new Board();
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
        board.setAuthor(author);
        board.setCreatedAt(LocalDateTime.now());
        return boardRepository.save(board);
    }

    @Transactional
    public void update(Long id, BoardForm form, String currentUsername) {
        Board board = boardRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
        if (!board.getAuthor().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("작성자만 수정할 수 있습니다.");
        }
        board.setTitle(form.getTitle());
        board.setContent(form.getContent());
    }

    @Transactional
    public void delete(Long id, String currentUsername) {
        Board board = boardRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
        if (!board.getAuthor().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("작성자만 삭제할 수 있습니다.");
        }
        boardRepository.delete(board);
    }
}
