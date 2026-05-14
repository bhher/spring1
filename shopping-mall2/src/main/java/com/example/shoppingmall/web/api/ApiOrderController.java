package com.example.shoppingmall.web.api;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.service.OrderService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class ApiOrderController {

	private final OrderService orderService;

	@PostMapping("/checkout")
	public ResponseEntity<Map<String, Object>> checkout(@AuthenticationPrincipal Member member) {
		var order = orderService.placeOrder(member);
		return ResponseEntity.ok(Map.of(
				"message", "주문이 완료되었습니다. (모의 결제)",
				"orderId", order.getId()));
	}

	@PostMapping("/{id}/cancel")
	public ResponseEntity<Map<String, String>> cancel(
			@AuthenticationPrincipal Member member,
			@PathVariable Long id) {
		orderService.cancelOrder(id, member);
		return ResponseEntity.ok(Map.of("message", "주문이 취소되었습니다."));
	}
}
