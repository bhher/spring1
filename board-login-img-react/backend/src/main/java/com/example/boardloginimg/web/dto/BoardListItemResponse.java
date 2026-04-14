package com.example.boardloginimg.web.dto;

import java.time.LocalDateTime;

public record BoardListItemResponse(
        Long id,
        String title,
        String authorName,
        LocalDateTime createdAt
) {
}
