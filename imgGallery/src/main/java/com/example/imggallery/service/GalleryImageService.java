package com.example.imggallery.service;

import com.example.imggallery.domain.GalleryImage;
import com.example.imggallery.repository.GalleryImageRepository;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GalleryImageService {

    private static final Set<String> ALLOWED_EXT = Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp");

    private final GalleryImageRepository galleryImageRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public GalleryImageService(GalleryImageRepository galleryImageRepository) {
        this.galleryImageRepository = galleryImageRepository;
    }

    @PostConstruct
    void ensureUploadDir() {
        Path dir = Paths.get(normalizeDir(uploadDir));
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            throw new IllegalStateException("업로드 폴더를 만들 수 없습니다: " + dir, e);
        }
    }

    @Transactional(readOnly = true)
    public List<GalleryImage> findAllForGallery() {
        return galleryImageRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public Resource loadAsResource(Long id) {
        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미지가 없습니다."));
        Path file = resolveStoredFile(image.getStoredFilename());
        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("파일이 없습니다: " + image.getStoredFilename());
        }
        try {
            return new UrlResource(file.toUri());
        } catch (IOException e) {
            throw new IllegalStateException("파일을 읽을 수 없습니다.", e);
        }
    }

    @Transactional
    public GalleryImage save(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("파일을 선택하세요.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
        }
        String original = file.getOriginalFilename();
        if (original == null || original.isBlank()) {
            original = "image";
        }
        String ext = extension(original);
        if (!ALLOWED_EXT.contains(ext)) {
            throw new IllegalArgumentException("허용 확장자: jpg, jpeg, png, gif, webp");
        }
        String stored = UUID.randomUUID() + ext;
        Path dest = resolveStoredFile(stored);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("저장 실패", e);
        }
        GalleryImage entity = new GalleryImage(original, stored, Instant.now());
        return galleryImageRepository.save(entity);
    }

    @Transactional
    public void deleteById(Long id) {
        GalleryImage image = galleryImageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("이미지가 없습니다."));
        Path file = resolveStoredFile(image.getStoredFilename());
        try {
            Files.deleteIfExists(file);
        } catch (IOException ignored) {
            // DB 삭제는 진행
        }
        galleryImageRepository.delete(image);
    }

    private Path resolveStoredFile(String storedFilename) {
        Path base = Paths.get(normalizeDir(uploadDir)).toAbsolutePath().normalize();
        Path file = base.resolve(storedFilename).normalize();
        if (!file.startsWith(base)) {
            throw new IllegalArgumentException("잘못된 경로입니다.");
        }
        return file;
    }

    private static String extension(String filename) {
        int i = filename.lastIndexOf('.');
        if (i < 0 || i == filename.length() - 1) {
            return "";
        }
        return filename.substring(i).toLowerCase(Locale.ROOT);
    }

    private static String normalizeDir(String dir) {
        if (dir == null || dir.isBlank()) {
            return "";
        }
        return dir.endsWith("/") || dir.endsWith("\\") ? dir : dir + File.separator;
    }
}
