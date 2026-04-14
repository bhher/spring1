package com.example.boardloginimg.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "board_images")
public class BoardImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalName;

    private String savedName;

    /** 디렉터리 경로 (끝에 구분자 포함) */
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id")
    private Board board;

    protected BoardImage() {
    }

    public BoardImage(String originalName, String savedName, String filePath, Board board) {
        this.originalName = originalName;
        this.savedName = savedName;
        this.filePath = filePath;
        this.board = board;
    }

    public Long getId() {
        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getSavedName() {
        return savedName;
    }

    public String getFilePath() {
        return filePath;
    }

    public Board getBoard() {
        return board;
    }

    public String getThumbnailSavedName() {
        return "s_" + savedName;
    }
}
