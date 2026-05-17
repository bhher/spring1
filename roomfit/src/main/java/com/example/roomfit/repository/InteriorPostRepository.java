package com.example.roomfit.repository;

import com.example.roomfit.domain.InteriorPost;
import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.domain.PostStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteriorPostRepository extends JpaRepository<InteriorPost, Long> {

	@EntityGraph(attributePaths = "images")
	Page<InteriorPost> findByStatus(PostStatus status, Pageable pageable);

	@EntityGraph(attributePaths = "images")
	Page<InteriorPost> findByStatusAndStyle(PostStatus status, InteriorStyle style, Pageable pageable);

	@EntityGraph(attributePaths = "images")
	List<InteriorPost> findTop50ByStatusOrderByCreatedAtDesc(PostStatus status);

	long countByStatus(PostStatus status);

	@EntityGraph(attributePaths = "images")
	List<InteriorPost> findTop20ByStatusOrderByLikeCountDescViewCountDescCreatedAtDesc(
			PostStatus status, Pageable pageable);

	@EntityGraph(attributePaths = {"images", "author"})
	Optional<InteriorPost> findByIdAndStatus(Long id, PostStatus status);
}
