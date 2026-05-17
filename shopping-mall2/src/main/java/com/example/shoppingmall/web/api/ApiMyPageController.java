package com.example.shoppingmall.web.api;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.dto.OrderDetailDto;
import com.example.shoppingmall.dto.OrderSummaryDto;
import com.example.shoppingmall.service.OrderService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class ApiMyPageController {

	private final OrderService orderService;

	@GetMapping("/orders")
	public List<OrderSummaryDto> orders(@AuthenticationPrincipal Member member) {
		return orderService.findMyOrders(member);
	}

	@GetMapping("/orders/{id}")
	public OrderDetailDto orderDetail(
			@AuthenticationPrincipal Member member,
			@PathVariable Long id) {
		return orderService.findOrderDetail(id, member);
	}
}
