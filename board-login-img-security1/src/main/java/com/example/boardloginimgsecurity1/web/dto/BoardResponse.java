package com.example.boardloginimgsecurity1.web.dto;

import com.example.boardloginimgsecurity1.domain.Board;
import com.example.boardloginimgsecurity1.domain.BoardImage;
import java.time.LocalDateTime;
import java.util.List;

public class BoardResponse {

    private final Long id;
    private final String title;
    private final String content;
    private final String authorUsername;
    private final String authorName;
    private final LocalDateTime createdAt;
    private final List<BoardImageResponse> images;

    public BoardResponse(
            Long id,
            String title,
            String content,
            String authorUsername,
            String authorName,
            LocalDateTime createdAt,
            List<BoardImageResponse> images) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorUsername = authorUsername;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.images = images;
    }

    public static BoardResponse listItem(Board board) {
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                null,
                board.getAuthor().getUsername(),
                board.getAuthor().getName(),
                board.getCreatedAt(),
                List.of());
    }

    public static BoardResponse fromBoard(Board board) {
        List<BoardImageResponse> imgs = board.getImages().stream()
                .map(BoardResponse::toImageResponse)
                .toList();
        return new BoardResponse(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                board.getAuthor().getUsername(),
                board.getAuthor().getName(),
                board.getCreatedAt(),
                imgs);
    }

    private static BoardImageResponse toImageResponse(BoardImage img) {
        String base = "/uploads/" + img.getSavedName();
        String thumb = "/uploads/" + img.getThumbnailSavedName();
        return new BoardImageResponse(img.getId(), img.getOriginalName(), base, thumb);
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorUsername() {
        return authorUsername;
    }

    public String getAuthorName() {
        return authorName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<BoardImageResponse> getImages() {
        return images;
    }
}
