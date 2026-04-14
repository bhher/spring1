package com.example.shoppingmall.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 주문 헤더. java.lang.Order 와 혼동을 피하기 위해 ShopOrder 라는 이름을 사용합니다.
 */
@Entity
@Table(name = "shop_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	/** 주문번호 (고유, 표시용) */
	@Column(nullable = false, unique = true, length = 40)
	private String orderNumber;

	@Column(nullable = false, precision = 14, scale = 2)
	private BigDecimal totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private OrderStatus status;

	@Column(nullable = false)
	private LocalDateTime orderedAt;

	/** 모의 결제 완료 시각 (실제 PG 연동 시에는 결제 키 등으로 확장) */
	@Column(nullable = false)
	private LocalDateTime paymentConfirmedAt;

	@Builder.Default
	@OneToMany(mappedBy = "shopOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<OrderItem> orderItems = new ArrayList<>();

	public void addOrderItem(OrderItem item) {
		orderItems.add(item);
		item.setShopOrder(this);
	}

	public void cancel() {
		if (this.status == OrderStatus.CANCELLED) {
			throw new IllegalStateException("이미 취소된 주문입니다.");
		}
		this.status = OrderStatus.CANCELLED;
	}
}
