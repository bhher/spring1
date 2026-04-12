package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    void index() {
        // 예상: data.sql 기준 전체 6개 게시글
        List<Article> expected = new ArrayList<>(Arrays.asList(
                new Article(1L, "가가가가", "1111"),
                new Article(2L, "나나나나", "2222"),
                new Article(3L, "다다다다", "3333"),
                new Article(4L, "당신의 인생 영화는?", "댓글 ㄱ"),
                new Article(5L, "당신의 소울 푸드는?", "댓글 ㄱㄱ"),
                new Article(6L, "당신의 취미는?", "댓글 ㄱㄱㄱ")));

        List<Article> articles = new ArrayList<>(articleService.index());
        expected.sort(Comparator.comparing(Article::getId));
        articles.sort(Comparator.comparing(Article::getId));

        assertEquals(expected.toString(), articles.toString());
    }

    @Test
    void show() {
        //예상
        Long id = 1L;
        Article expected = new Article(id,"가가가가","1111");

        //실제
        Article article = articleService.show(id);

        assertNotNull(article);
        assertEquals(expected.getId(), article.getId());
        assertEquals(expected.getTitle(), article.getTitle());
        assertEquals(expected.getContent(), article.getContent());
    }

    @Test
    @Transactional
    void create() {
        String title = "라라라라";
        String content = "4444";
        ArticleForm dto = new ArticleForm(null, title, content);
        // data.sql 이후 다음 ID는 7L
        Article expected = new Article(7L, title, content);

        Article article = articleService.create(dto);

        assertNotNull(article);
        assertEquals(expected.getId(), article.getId());
        assertEquals(expected.getTitle(), article.getTitle());
        assertEquals(expected.getContent(), article.getContent());
    }
}