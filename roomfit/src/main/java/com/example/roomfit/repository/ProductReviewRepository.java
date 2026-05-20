package com.example.roomfit.repository;

import com.example.roomfit.domain.ProductReview;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

	@EntityGraph(attributePaths = "member")
	List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);

	Optional<ProductReview> findByProductIdAndMemberId(Long productId, Long memberId);
}
