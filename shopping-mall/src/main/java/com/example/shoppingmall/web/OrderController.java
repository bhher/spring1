package com.example.shoppingmall.web;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.domain.ShopOrder;
import com.example.shoppingmall.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 장바구니에서 주문 생성·취소 (모의 결제).
 */
@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping("/checkout")
	public String checkout(@AuthenticationPrincipal Member member, RedirectAttributes ra) {
		ShopOrder order = orderService.placeOrder(member);
		ra.addFlashAttribute("message", "주문이 완료되었습니다. (모의 결제)");
		return "redirect:/mypage/orders/" + order.getId();
	}

	@PostMapping("/{id}/cancel")
	public String cancel(
			@AuthenticationPrincipal Member member,
			@PathVariable Long id,
			RedirectAttributes ra) {
		orderService.cancelOrder(id, member);
		ra.addFlashAttribute("message", "주문이 취소되었습니다.");
		return "redirect:/mypage/orders/" + id;
	}
}
