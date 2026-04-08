package com.example.firstproject.repository;

import com.example.firstproject.entity.Article;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ArticleRepository extends CrudRepository<Article,Long> {
    //jpa는 정렬 페이지 나누기 가능
    //crud는 간소화 된 레파지토리

    @Override
    List<Article> findAll();
}
