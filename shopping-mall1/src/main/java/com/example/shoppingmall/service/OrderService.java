package com.example.shoppingmall.service;

import com.example.shoppingmall.domain.Cart;
import com.example.shoppingmall.domain.CartItem;
import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.domain.OrderItem;
import com.example.shoppingmall.domain.OrderStatus;
import com.example.shoppingmall.domain.Product;
import com.example.shoppingmall.domain.Role;
import com.example.shoppingmall.domain.ShopOrder;
import com.example.shoppingmall.dto.OrderDetailDto;
import com.example.shoppingmall.dto.OrderDetailDto.OrderLineDto;
import com.example.shoppingmall.dto.OrderSummaryDto;
import com.example.shoppingmall.exception.BusinessException;
import com.example.shoppingmall.exception.ForbiddenAccessException;
import com.example.shoppingmall.exception.ResourceNotFoundException;
import com.example.shoppingmall.repository.CartRepository;
import com.example.shoppingmall.repository.ProductRepository;
import com.example.shoppingmall.repository.ShopOrderRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final CartRepository cartRepository;
	private final ShopOrderRepository shopOrderRepository;
	private final ProductRepository productRepository;

	@Transactional
	public ShopOrder placeOrder(Member member) {
		Cart cart = cartRepository.findWithItemsByMember_Id(member.getId())
				.orElseThrow(() -> new BusinessException("장바구니가 비었습니다."));
		if (cart.getItems().isEmpty()) {
			throw new BusinessException("장바구니가 비었습니다.");
		}
		BigDecimal total = BigDecimal.ZERO;
		for (CartItem line : cart.getItems()) {
			Product p = line.getProduct();
			if (p.getStockQuantity() < line.getQuantity()) {
				throw new BusinessException("재고 부족: " + p.getName());
			}
			BigDecimal lineAmt = p.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
			total = total.add(lineAmt);
		}
		String orderNumber = "ORD-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
		LocalDateTime now = LocalDateTime.now();
		ShopOrder order = ShopOrder.builder()
				.member(member)
				.orderNumber(orderNumber)
				.totalAmount(total)
				.status(OrderStatus.ORDERED)
				.orderedAt(now)
				.paymentConfirmedAt(now)
				.build();
		for (CartItem line : cart.getItems()) {
			Product p = line.getProduct();
			p.decreaseStock(line.getQuantity());
			BigDecimal lineTotal = p.getPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
			OrderItem oi = OrderItem.builder()
					.productId(p.getId())
					.productName(p.getName())
					.unitPrice(p.getPrice())
					.quantity(line.getQuantity())
					.lineTotal(lineTotal)
					.build();
			order.addOrderItem(oi);
		}
		shopOrderRepository.save(order);
		cart.getItems().clear();
		cartRepository.save(cart);
		return order;
	}

	@Transactional
	public void cancelOrder(Long orderId, Member member) {
		ShopOrder order = shopOrderRepository.findWithItemsAndMemberById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("주문을 찾을 수 없습니다."));
		if (!order.getMember().getId().equals(member.getId()) && member.getRole() != Role.ADMIN) {
			throw new ForbiddenAccessException("본인 주문만 취소할 수 있습니다.");
		}
		if (order.getStatus() == OrderStatus.CANCELLED) {
			throw new BusinessException("이미 취소된 주문입니다.");
		}
		for (OrderItem oi : order.getOrderItems()) {
			if (oi.getProductId() != null) {
				productRepository.findById(oi.getProductId()).ifPresent(p -> p.increaseStock(oi.getQuantity()));
			}
		}
		order.cancel();
	}

	@Transactional(readOnly = true)
	public List<OrderSummaryDto> findMyOrders(Member member) {
		return shopOrderRepository.findByMember_IdOrderByOrderedAtDesc(member.getId()).stream()
				.map(o -> new OrderSummaryDto(o.getId(), o.getOrderNumber(), o.getTotalAmount(), o.getStatus(), o.getOrderedAt()))
				.toList();
	}

	@Transactional(readOnly = true)
	public OrderDetailDto findOrderDetail(Long orderId, Member member) {
		ShopOrder order = shopOrderRepository.findWithItemsAndMemberById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("주문을 찾을 수 없습니다."));
		if (!order.getMember().getId().equals(member.getId()) && member.getRole() != Role.ADMIN) {
			throw new ForbiddenAccessException("본인 주문만 조회할 수 있습니다.");
		}
		List<OrderLineDto> lines = order.getOrderItems().stream()
				.map(oi -> new OrderLineDto(oi.getProductId(), oi.getProductName(), oi.getUnitPrice(), oi.getQuantity(), oi.getLineTotal()))
				.toList();
		return new OrderDetailDto(
				order.getId(),
				order.getOrderNumber(),
				order.getTotalAmount(),
				order.getStatus(),
				order.getOrderedAt(),
				order.getPaymentConfirmedAt(),
				lines);
	}
}
