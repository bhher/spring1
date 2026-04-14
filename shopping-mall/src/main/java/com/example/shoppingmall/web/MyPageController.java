package com.example.shoppingmall.web;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.dto.OrderDetailDto;
import com.example.shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 마이페이지: 내 주문 목록·상세.
 */
@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

	private final OrderService orderService;

	@GetMapping("/orders")
	public String orders(@AuthenticationPrincipal Member member, Model model) {
		model.addAttribute("orders", orderService.findMyOrders(member));
		return "mypage/orders";
	}

	@GetMapping("/orders/{id}")
	public String orderDetail(
			@AuthenticationPrincipal Member member,
			@PathVariable Long id,
			Model model) {
		OrderDetailDto dto = orderService.findOrderDetail(id, member);
		model.addAttribute("order", dto);
		return "mypage/order-detail";
	}
}
