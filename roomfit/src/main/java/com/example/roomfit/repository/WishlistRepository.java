package com.example.roomfit.repository;

import com.example.roomfit.domain.Wishlist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

	boolean existsByMemberIdAndProductId(Long memberId, Long productId);

	Optional<Wishlist> findByMemberIdAndProductId(Long memberId, Long productId);

	List<Wishlist> findByMemberIdOrderByCreatedAtDesc(Long memberId);
}
