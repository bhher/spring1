package com.example.firstproject.entity;

import com.example.firstproject.dto.CommentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "article_id")
    private Article article;

    @Column
    private String nickname;

    @Column
    private String body;

    public static Comment createComment(CommentDto dto, Article article) {
        // 예외 발생
        if(dto.getId() != null) // 받아온 데이터에 id가 있다면
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 id가 없어야 합니다.");

        if(dto.getArticleId() != article.getId()) // 요청url의 id(articleId)와 요청데이터에 article_id가 다르면
            throw new IllegalArgumentException("댓글 생성 실패! 게시글의 id가 잘못되었습니다.");

        // 엔티티 생성 및 반환
        return Comment.builder()
                .id(dto.getId())
                .article(article)
                .nickname(dto.getNickname())
                .body(dto.getBody())
                .build();
    }

    public void patch(CommentDto dto) {
        // 예외 발생
        if(this.id != dto.getId()) { // 요청URL id와 댓글의 id가 다를때
            throw new IllegalArgumentException("댓글 수정 실패! 잘못된 id가 입력되었습니다.");
        }

        // 객체를 갱신
        if(dto.getNickname() != null) {
            this.nickname = dto.getNickname();
        }

        if(dto.getBody() != null) {
            this.body = dto.getBody();
        }
    }
}
