package com.example.roomfit.repository;

import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.domain.Product;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

	List<Product> findByOnSaleTrueAndStyleTagAndPriceLessThanEqualOrderByAvgRatingDesc(
			InteriorStyle styleTag, int maxPrice);

	List<Product> findByOnSaleTrueOrderByAvgRatingDesc();
}
