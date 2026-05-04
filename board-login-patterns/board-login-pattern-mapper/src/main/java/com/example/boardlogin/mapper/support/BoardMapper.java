package com.example.boardlogin.mapper.support;

import com.example.boardlogin.mapper.domain.Board;
import com.example.boardlogin.mapper.domain.User;
import com.example.boardlogin.mapper.web.dto.BoardForm;
import java.time.LocalDateTime;

public final class BoardMapper {

    private BoardMapper() {
    }

    public static Board toNewEntity(BoardForm form, User author, LocalDateTime createdAt) {
        return new Board(form.getTitle(), form.getContent(), author, createdAt);
    }
}
