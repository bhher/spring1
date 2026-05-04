package com.example.boardloginimgsecurity1.web;

import com.example.boardloginimgsecurity1.domain.User;
import com.example.boardloginimgsecurity1.security.LoginUserDetails;
import com.example.boardloginimgsecurity1.service.BoardService;
import com.example.boardloginimgsecurity1.service.UserService;
import com.example.boardloginimgsecurity1.web.dto.BoardResponse;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
public class BoardRestController {

    private final BoardService boardService;
    private final UserService userService;

    public BoardRestController(BoardService boardService, UserService userService) {
        this.boardService = boardService;
        this.userService = userService;
    }

    @GetMapping
    public List<BoardResponse> list() {
        return boardService.findAll().stream().map(BoardResponse::listItem).toList();
    }

    @GetMapping("/{id:\\d+}")
    public BoardResponse detail(@PathVariable Long id) {
        return BoardResponse.fromBoard(boardService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BoardResponse> create(
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> files,
            @AuthenticationPrincipal LoginUserDetails principal) {
        User author = userService.findByUsername(principal.getUsername());
        if (author == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var board = boardService.create(title, content, author, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(BoardResponse.fromBoard(board));
    }

    @PutMapping(path = "/{id:\\d+}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BoardResponse update(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String content,
            @RequestParam(required = false) List<MultipartFile> files,
            @AuthenticationPrincipal LoginUserDetails principal) {
        boardService.update(id, title, content, principal.getUsername(), files);
        return BoardResponse.fromBoard(boardService.findById(id));
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal LoginUserDetails principal) {
        boardService.delete(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
