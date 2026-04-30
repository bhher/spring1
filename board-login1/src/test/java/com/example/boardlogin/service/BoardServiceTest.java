package com.example.boardlogin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.boardlogin.domain.Board;
import com.example.boardlogin.domain.User;
import com.example.boardlogin.repository.BoardRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BoardServiceTest {

    @Mock
    private BoardRepository boardRepository;

    @InjectMocks
    private BoardService boardService;

    private final User author = new User("writer", "pw", "작성자");

    @Test
    @DisplayName("목록 — repository 결과 그대로")
    void findAll() {
        Board b = new Board("t", "c", author, java.time.LocalDateTime.now());
        when(boardRepository.findAll()).thenReturn(List.of(b));

        assertEquals(1, boardService.findAll().size());
        assertEquals("t", boardService.findAll().get(0).getTitle());
    }

    @Test
    @DisplayName("상세 — 있으면 Board, 없으면 예외")
    void findById() {
        Board b = new Board("t", "c", author, java.time.LocalDateTime.now());
        when(boardRepository.findById(1L)).thenReturn(Optional.of(b));

        assertEquals("t", boardService.findById(1L).getTitle());

        when(boardRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> boardService.findById(99L));
    }

    @Test
    @DisplayName("생성 — save 호출 후 반환")
    void create() {
        Board saved = new Board("제목", "내용", author, java.time.LocalDateTime.now());
        when(boardRepository.save(any(Board.class))).thenReturn(saved);

        Board result = boardService.create("제목", "내용", author);

        assertEquals("제목", result.getTitle());
        verify(boardRepository).save(any(Board.class));
    }

    @Test
    @DisplayName("수정 — 작성자와 같으면 성공")
    void update_ok() {
        Board board = new Board("옛제목", "옛내용", author, java.time.LocalDateTime.now());
        when(boardRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(board));

        boardService.update(1L, "새제목", "새내용", "writer");

        assertEquals("새제목", board.getTitle());
        assertEquals("새내용", board.getContent());
    }

    @Test
    @DisplayName("수정 — 작성자 아니면 IllegalStateException")
    void update_forbidden() {
        Board board = new Board("t", "c", author, java.time.LocalDateTime.now());
        when(boardRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(board));

        assertThrows(
                IllegalStateException.class,
                () -> boardService.update(1L, "a", "b", "other"));
    }

    @Test
    @DisplayName("삭제 — 작성자와 같으면 delete 호출")
    void delete_ok() {
        Board board = new Board("t", "c", author, java.time.LocalDateTime.now());
        when(boardRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(board));

        boardService.delete(1L, "writer");

        verify(boardRepository).delete(board);
    }

    @Test
    @DisplayName("삭제 — 작성자 아니면 IllegalStateException")
    void delete_forbidden() {
        Board board = new Board("t", "c", author, java.time.LocalDateTime.now());
        when(boardRepository.findByIdWithAuthor(1L)).thenReturn(Optional.of(board));

        assertThrows(IllegalStateException.class, () -> boardService.delete(1L, "hacker"));
    }
}
