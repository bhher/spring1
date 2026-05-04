package com.example.thymeleafexamples.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * {@code /demo/loop} 예제용 카탈로그 행. H2에 저장되며 Repository → Service → Controller 로 조회합니다.
 */
@Entity
@Table(name = "demo_items")
public class DemoItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false)
	private int qty;

	protected DemoItem() {
	}

	public DemoItem(String name, int qty) {
		this.name = name;
		this.qty = qty;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getQty() {
		return qty;
	}
}
