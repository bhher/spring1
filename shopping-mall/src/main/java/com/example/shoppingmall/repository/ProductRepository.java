package com.example.shoppingmall.repository;

import com.example.shoppingmall.domain.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

	@EntityGraph(attributePaths = "images")
	@Query("SELECT p FROM Product p")
	Page<Product> findAllWithImages(Pageable pageable);

	@EntityGraph(attributePaths = "images")
	@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
	Page<Product> searchByName(@Param("keyword") String keyword, Pageable pageable);

	@EntityGraph(attributePaths = {"images"})
	@Query("SELECT p FROM Product p WHERE p.id = :id")
	Optional<Product> findWithImagesById(@Param("id") Long id);
}
