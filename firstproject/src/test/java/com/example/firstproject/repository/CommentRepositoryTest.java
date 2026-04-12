package com.example.firstproject.repository;

import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    @DisplayName("특정 게시글의 모든 댓글 조회")
    void findByArticleId() {
        // 준비
        Long articleId = 4L;
        // 수행
        List<Comment> comments = commentRepository.findByArticleId(articleId);
        comments.sort(Comparator.comparing(Comment::getId));

        assertEquals(3, comments.size());
        assertEquals(List.of(1L, 2L, 3L), comments.stream().map(Comment::getId).collect(Collectors.toList()));
        assertEquals("Park", comments.get(0).getNickname());
        assertEquals("Kim", comments.get(1).getNickname());
        assertEquals("Choi", comments.get(2).getNickname());
        assertEquals(articleId, comments.get(0).getArticle().getId());
    }

    @Test
    void findByNickname() {
        // 준비
        String nickname = "Park";
        // 수행
        List<Comment> comments = commentRepository.findByNickname(nickname);
        comments.sort(Comparator.comparing(Comment::getId));

        assertEquals(3, comments.size());
        assertEquals(List.of(1L, 4L, 7L), comments.stream().map(Comment::getId).collect(Collectors.toList()));
        assertEquals(List.of(4L, 5L, 6L),
                comments.stream().map(c -> c.getArticle().getId()).collect(Collectors.toList()));
        assertEquals("굳 윌 헌팅", comments.get(0).getBody());
        assertEquals("치킨", comments.get(1).getBody());
        assertEquals("조깅", comments.get(2).getBody());
    }

}