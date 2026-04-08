package com.example.firstproject.repository;

import com.example.firstproject.entity.Article;
import com.example.firstproject.entity.Comment;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Arrays;
import java.util.List;

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
        // 예상
        Article article = new Article(4L, "당신의 인생 영화는?", "댓글 ㄱ");
        Comment a = new Comment(1L, article, "Park", "굳 윌 헌팅");
        Comment b = new Comment(2L, article, "Kim", "아이 엠 샘");
        Comment c = new Comment(3L, article, "Choi", "쇼생크의 탈출");
        List<Comment> expected = Arrays.asList(a, b, c);
        System.out.println(expected.toString());
        System.out.println(comments.toString());
        // 검증
        assertEquals(expected.toString(), comments.toString(), "4번 글의 모든 댓글을 출력!");

    }

    @Test
    void findByNickname() {
        // 준비
        String nickname = "Park";
        // 수행
        List<Comment> comments = commentRepository.findByNickname(nickname);
        // 예상
        Comment a = new Comment(1L, new Article(4L, "당신의 인생 영화는?", "댓글 ㄱ"), nickname, "굳 윌 헌팅");
        Comment b = new Comment(4L, new Article(5L, "당신의 소울 푸드는?", "댓글 ㄱㄱ"), nickname, "치킨");
        Comment c = new Comment(7L, new Article(6L, "당신의 취미는?", "댓글 ㄱㄱㄱ"), nickname, "조깅");
        List<Comment> expected = Arrays.asList(a, b, c);

        System.out.println(comments.toString());
        System.out.println(expected.toString());
        assertEquals(expected.toString(),comments.toString(),"박의 모든 댓글 출력");
    }

}