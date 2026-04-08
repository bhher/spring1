package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ArticleServiceTest {

    @Autowired
    private ArticleService articleService;

    @Test
    void index() {
        // 예상
        Article a = new Article(1L, "가가가가", "1111");
        Article b = new Article(2L, "나나나나", "2222");
        Article c = new Article(3L, "다다다다", "3333");
        List<Article> expected = new ArrayList<Article>(Arrays.asList(a, b, c));

        //실제
        List<Article> articles = articleService.index();

        System.out.println(expected);
        System.out.println(articles);

        assertEquals(expected.toString(),articles.toString());
    }

    @Test
    void show() {
        //예상
        Long id = 1L;
        Article expected = new Article(id,"가가가가","1111");

        //실제
        Article article = articleService.show(id);

        //비교
        assertEquals(expected.toString(),article.toString());
    }

    @Test
    @Transactional
    void create(){
        String title = "라라라라";
        String content = "4444";
        ArticleForm dto = new ArticleForm(null,title,content);
        Article expected = new Article(4L,title,content);

        //실제
        Article article = articleService.create(dto);

        assertEquals(expected.toString(), article.toString());
    }
}