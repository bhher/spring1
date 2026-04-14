package com.example.shoppingmall.web;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 로그인 사용자 장바구니.
 */
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	private final CartService cartService;

	@GetMapping
	public String cart(@AuthenticationPrincipal Member member, Model model) {
		model.addAttribute("lines", cartService.getCartLines(member));
		return "cart";
	}

	@PostMapping("/items")
	public String addItem(
			@AuthenticationPrincipal Member member,
			@RequestParam Long productId,
			@RequestParam(defaultValue = "1") int quantity,
			RedirectAttributes ra) {
		cartService.addItem(member, productId, quantity);
		ra.addFlashAttribute("message", "장바구니에 담았습니다.");
		return "redirect:/cart";
	}

	@PostMapping("/items/{itemId}/quantity")
	public String updateQuantity(
			@AuthenticationPrincipal Member member,
			@PathVariable Long itemId,
			@RequestParam int quantity) {
		cartService.updateQuantity(member, itemId, quantity);
		return "redirect:/cart";
	}

	@PostMapping("/items/{itemId}/remove")
	public String remove(
			@AuthenticationPrincipal Member member,
			@PathVariable Long itemId,
			RedirectAttributes ra) {
		cartService.removeItem(member, itemId);
		ra.addFlashAttribute("message", "삭제했습니다.");
		return "redirect:/cart";
	}
}
