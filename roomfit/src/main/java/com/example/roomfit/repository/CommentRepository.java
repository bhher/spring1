package com.example.roomfit.repository;

import com.example.roomfit.domain.Comment;
import com.example.roomfit.domain.PostStatus;
import com.example.roomfit.domain.PostType;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@EntityGraph(attributePaths = "author")
	List<Comment> findByPostTypeAndPostIdAndStatusOrderByCreatedAtAsc(
			PostType postType, Long postId, PostStatus status);

	long countByPostTypeAndPostIdAndStatus(PostType postType, Long postId, PostStatus status);
}
