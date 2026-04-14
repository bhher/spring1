package com.example.boardloginimg.web;

import com.example.boardloginimg.domain.Board;
import com.example.boardloginimg.domain.BoardImage;
import com.example.boardloginimg.domain.User;
import com.example.boardloginimg.service.BoardService;
import com.example.boardloginimg.service.UserService;
import com.example.boardloginimg.web.dto.BoardDetailResponse;
import com.example.boardloginimg.web.dto.BoardListItemResponse;
import com.example.boardloginimg.web.dto.ImageResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/posts")
public class BoardApiController {

    private final BoardService boardService;
    private final UserService userService;

    public BoardApiController(BoardService boardService, UserService userService) {
        this.boardService = boardService;
        this.userService = userService;
    }

    @GetMapping
    public List<BoardListItemResponse> list() {
        return boardService.findAll().stream()
                .map(b -> new BoardListItemResponse(
                        b.getId(),
                        b.getTitle(),
                        b.getAuthor().getName(),
                        b.getCreatedAt()))
                .toList();
    }

    @GetMapping("/{id}")
    public BoardDetailResponse detail(@PathVariable Long id, HttpSession session) {
        Board board = boardService.findById(id);
        return toDetail(board, canEdit(session, board));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BoardDetailResponse create(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> files,
            HttpSession session) {
        String username = (String) session.getAttribute(AuthApiController.SESSION_USER);
        User author = userService.findByUsername(username);
        if (author == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
        Board saved = boardService.create(title, content, author, files);
        return toDetail(saved, true);
    }

    @PostMapping(value = "/{id}/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BoardDetailResponse update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> files,
            HttpSession session) {
        String username = (String) session.getAttribute(AuthApiController.SESSION_USER);
        boardService.update(id, title, content, username, files);
        Board board = boardService.findById(id);
        return toDetail(board, true);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, HttpSession session) {
        String username = (String) session.getAttribute(AuthApiController.SESSION_USER);
        boardService.delete(id, username);
    }

    private static BoardDetailResponse toDetail(Board board, boolean canEdit) {
        List<ImageResponse> imgs = board.getImages().stream()
                .map(BoardApiController::toImage)
                .toList();
        return new BoardDetailResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getAuthor().getUsername(),
                board.getAuthor().getName(),
                board.getCreatedAt(),
                canEdit,
                imgs);
    }

    private static ImageResponse toImage(BoardImage img) {
        String base = "/uploads/";
        return new ImageResponse(
                img.getId(),
                img.getOriginalName(),
                base + img.getSavedName(),
                base + img.getThumbnailSavedName());
    }

    private static boolean canEdit(HttpSession session, Board board) {
        Object u = session.getAttribute(AuthApiController.SESSION_USER);
        if (u == null) {
            return false;
        }
        return u.toString().equals(board.getAuthor().getUsername());
    }
}
