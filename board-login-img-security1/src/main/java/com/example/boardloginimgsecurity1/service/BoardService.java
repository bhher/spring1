package com.example.boardloginimgsecurity1.service;

import com.example.boardloginimgsecurity1.domain.Board;
import com.example.boardloginimgsecurity1.domain.BoardImage;
import com.example.boardloginimgsecurity1.domain.User;
import com.example.boardloginimgsecurity1.repository.BoardRepository;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class BoardService {

    private final BoardRepository boardRepository;

    @Value("${app.upload-dir}")
    private String uploadDir;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @PostConstruct
    void ensureUploadDir() {
        File dir = new File(normalizeDir(uploadDir));
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalStateException("업로드 폴더를 만들 수 없습니다: " + uploadDir);
        }
    }

    private String normalizeDir(String dir) {
        if (dir == null || dir.isBlank()) {
            return "";
        }
        return dir.endsWith("/") || dir.endsWith("\\") ? dir : dir + File.separator;
    }

    @Transactional(readOnly = true)
    public List<Board> findAll() {
        return boardRepository.findAllWithAuthor();
    }

    @Transactional(readOnly = true)
    public Board findById(Long id) {
        return boardRepository.findByIdWithAuthorAndImages(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
    }

    @Transactional
    public Board create(String title, String content, User author, List<MultipartFile> files) {
        Board board = new Board(title, content, author, LocalDateTime.now());
        boardRepository.save(board);
        storeImages(board, files);
        return boardRepository.findByIdWithAuthorAndImages(board.getId()).orElse(board);
    }

    @Transactional
    public void update(Long id, String title, String content, String currentUsername, List<MultipartFile> files) {
        Board board = boardRepository.findByIdWithAuthorAndImages(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
        if (!board.getAuthor().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("작성자만 수정할 수 있습니다.");
        }
        board.update(title, content);
        storeImages(board, files);
    }

    @Transactional
    public void delete(Long id, String currentUsername) {
        Board board = boardRepository.findByIdWithAuthorAndImages(id)
                .orElseThrow(() -> new IllegalArgumentException("글이 없습니다."));
        if (!board.getAuthor().getUsername().equals(currentUsername)) {
            throw new IllegalStateException("작성자만 삭제할 수 있습니다.");
        }
        deleteImageFiles(board);
        boardRepository.delete(board);
    }

    private void storeImages(Board board, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) {
            return;
        }
        String dir = normalizeDir(uploadDir);
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            try {
                String originalName = file.getOriginalFilename();
                if (originalName == null || originalName.isBlank()) {
                    originalName = "file";
                }
                String savedName = UUID.randomUUID() + "_" + originalName;
                File saveFile = new File(dir + savedName);
                file.transferTo(saveFile);

                File thumbFile = new File(dir + "s_" + savedName);
                Thumbnails.of(saveFile).size(200, 200).toFile(thumbFile);

                BoardImage image = new BoardImage(originalName, savedName, dir, board);
                board.getImages().add(image);
            }
            catch (IOException e) {
                throw new RuntimeException("이미지 업로드 실패", e);
            }
        }
        boardRepository.save(board);
    }

    private void deleteImageFiles(Board board) {
        for (BoardImage img : board.getImages()) {
            String base = img.getFilePath() != null ? img.getFilePath() : normalizeDir(uploadDir);
            File f = new File(base + img.getSavedName());
            File t = new File(base + img.getThumbnailSavedName());
            if (f.exists()) {
                //noinspection ResultOfMethodCallIgnored
                f.delete();
            }
            if (t.exists()) {
                //noinspection ResultOfMethodCallIgnored
                t.delete();
            }
        }
    }
}
