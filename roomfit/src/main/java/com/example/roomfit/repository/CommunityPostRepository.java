package com.example.roomfit.repository;

import com.example.roomfit.domain.CommunityBoardType;
import com.example.roomfit.domain.CommunityPost;
import com.example.roomfit.domain.PostStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

	@EntityGraph(attributePaths = "author")
	Page<CommunityPost> findByBoardTypeAndStatus(
			CommunityBoardType boardType, PostStatus status, Pageable pageable);

	@EntityGraph(attributePaths = "author")
	Optional<CommunityPost> findByIdAndStatus(Long id, PostStatus status);

	long countByStatus(PostStatus status);
}
