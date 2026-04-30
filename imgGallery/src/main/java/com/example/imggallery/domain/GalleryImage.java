package com.example.imggallery.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "gallery_images")
public class GalleryImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String originalFilename;

    /** 디스크 파일명 (UUID + 확장자) */
    @Column(nullable = false, length = 255, unique = true)
    private String storedFilename;

    @Column(nullable = false)
    private Instant createdAt;

    protected GalleryImage() {
    }

    public GalleryImage(String originalFilename, String storedFilename, Instant createdAt) {
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
