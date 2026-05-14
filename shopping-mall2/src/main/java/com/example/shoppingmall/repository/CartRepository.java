package com.example.shoppingmall.repository;

import com.example.shoppingmall.domain.Cart;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

	Optional<Cart> findByMember_Id(Long memberId);

	@EntityGraph(attributePaths = {"items", "items.product", "items.product.images"})
	Optional<Cart> findWithItemsByMember_Id(Long memberId);
}
