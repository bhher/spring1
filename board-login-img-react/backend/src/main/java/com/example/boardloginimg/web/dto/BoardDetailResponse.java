package com.example.boardloginimg.web.dto;

import java.time.LocalDateTime;
import java.util.List;

public record BoardDetailResponse(
        Long id,
        String title,
        String content,
        String authorUsername,
        String authorName,
        LocalDateTime createdAt,
        boolean canEdit,
        List<ImageResponse> images
) {
}
