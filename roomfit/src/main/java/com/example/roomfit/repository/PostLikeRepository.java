package com.example.roomfit.repository;

import com.example.roomfit.domain.PostLike;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	boolean existsByMemberIdAndPostId(Long memberId, Long postId);

	Optional<PostLike> findByMemberIdAndPostId(Long memberId, Long postId);

	long countByPostId(Long postId);

	@Query("SELECT pl.post.id FROM PostLike pl WHERE pl.member.id IN :memberIds")
	List<Long> findPostIdsLikedByMembers(List<Long> memberIds);

	List<PostLike> findByMemberId(Long memberId);
}
