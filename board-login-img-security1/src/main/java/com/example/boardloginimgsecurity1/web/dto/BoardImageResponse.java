package com.example.boardloginimgsecurity1.web.dto;

public class BoardImageResponse {

    private final Long id;
    private final String originalName;
    private final String url;
    private final String thumbnailUrl;

    public BoardImageResponse(Long id, String originalName, String url, String thumbnailUrl) {
        this.id = id;
        this.originalName = originalName;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getUrl() {
        return url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }
}
