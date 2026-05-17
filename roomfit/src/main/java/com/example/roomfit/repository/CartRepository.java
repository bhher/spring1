package com.example.roomfit.repository;

import com.example.roomfit.domain.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

	@EntityGraph(attributePaths = {"items", "items.product"})
	Optional<Cart> findWithItemsByMemberId(Long memberId);

	Optional<Cart> findByMemberId(Long memberId);
}
