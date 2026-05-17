package com.example.roomfit.repository;

import com.example.roomfit.domain.CommunityBoardType;
import com.example.roomfit.domain.CommunityPost;
import com.example.roomfit.domain.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

	Page<CommunityPost> findByBoardTypeAndStatus(
			CommunityBoardType boardType, PostStatus status, Pageable pageable);

	long countByStatus(PostStatus status);
}
