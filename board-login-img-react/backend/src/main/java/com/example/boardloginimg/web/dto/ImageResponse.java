package com.example.boardloginimg.web.dto;

public record ImageResponse(
        Long id,
        String originalName,
        String url,
        String thumbnailUrl
) {
}
