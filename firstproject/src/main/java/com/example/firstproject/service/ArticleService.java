package com.example.firstproject.service;

import com.example.firstproject.dto.ArticleForm;
import com.example.firstproject.entity.Article;
import com.example.firstproject.repository.ArticleRepository;
import com.example.firstproject.repository.CommentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class ArticleService {
    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CommentRepository commentRepository;

    public List<Article> index() {
        return articleRepository.findAll();
    }

    public Article show(Long id) {
        return articleRepository.findById(id).orElse(null);
    }

    public Article create(ArticleForm dto) {
        Article article = dto.toEntity();

        // 생성할때 id를 넣었으면 null을 리턴
        if(article.getId() != null) {
            return null;
        }

        return  articleRepository.save(article);

    }

    public Article update(Long id, ArticleForm dto) {
        // dto를 Entity로 변경
        Article article = dto.toEntity();
        // target 찾기
        Article target = articleRepository.findById(id).orElse(null);
        // 잘못된 요청이면 null
        if(target == null || id != article.getId()) {
            return null;
        }
        // 업데이트 후 updated 리턴
        target.patch(article);
        Article updated = articleRepository.save(target);
        return updated;
    }

    public Article delete(Long id) {
        Article target = articleRepository.findById(id).orElse(null);

        if(target == null) {
            return null;
        }
        articleRepository.delete(target);
        return target;
    }

}
