package com.example.roomfit.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 200)
	private String name;

	@Column(nullable = false)
	private int price;

	@Builder.Default
	private int stock = 100;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private InteriorStyle styleTag;

	private BigDecimal roomSizeMin;
	private BigDecimal roomSizeMax;

	@Column(length = 255)
	private String imagePath;

	@Builder.Default
	private double avgRating = 0.0;

	@Builder.Default
	private int reviewCount = 0;

	@Builder.Default
	private boolean onSale = true;
}
