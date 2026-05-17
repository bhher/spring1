package com.example.shoppingmall.repository;

import com.example.shoppingmall.domain.ShopOrder;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {

	@EntityGraph(attributePaths = {"orderItems", "member"})
	@Query("SELECT o FROM ShopOrder o WHERE o.id = :id")
	Optional<ShopOrder> findWithItemsAndMemberById(@Param("id") Long id);

	List<ShopOrder> findByMember_IdOrderByOrderedAtDesc(Long memberId);

	Optional<ShopOrder> findByOrderNumber(String orderNumber);
}
