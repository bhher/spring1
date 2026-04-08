package com.example.boardlogin.service;

import com.example.boardlogin.domain.Board;
import com.example.boardlogin.domain.User;
import com.example.boardlogin.repository.BoardRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Board create(String title, String content, User author) {
        Board board = new Board(title, content, author, LocalDateTime.now());
        return boardRepository.save(board);
    }

    @Transactional
    public void update(Long id, String title, String content, String currentUsername) {
        Board board = boardRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
        if (!board.getAuthor().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("작성자만 수정할 수 있습니다.");
        }
        board.update(title, content);
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
