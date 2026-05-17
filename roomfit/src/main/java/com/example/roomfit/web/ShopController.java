package com.example.roomfit.web;

import com.example.roomfit.domain.Member;
import com.example.roomfit.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/shop")
@RequiredArgsConstructor
public class ShopController {

	private final ShopService shopService;

	@GetMapping
	public String list(Model model) {
		model.addAttribute("products", shopService.listAll());
		return "shop/list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, @AuthenticationPrincipal Member member, Model model) {
		model.addAttribute("product", shopService.getProduct(id));
		model.addAttribute("reviews", shopService.getReviews(id));
		if (member != null) {
			model.addAttribute("wished", shopService.isWished(member.getId(), id));
		}
		return "shop/detail";
	}

	@PostMapping("/{id}/cart")
	public String addCart(@PathVariable Long id, @AuthenticationPrincipal Member member) {
		shopService.addToCart(member.getId(), id, 1);
		return "redirect:/shop/cart";
	}

	@GetMapping("/cart")
	public String cart(@AuthenticationPrincipal Member member, Model model) {
		model.addAttribute("cart", shopService.getCartWithItems(member.getId()));
		return "shop/cart";
	}

	@PostMapping("/{id}/wish")
	public String wish(@PathVariable Long id, @AuthenticationPrincipal Member member) {
		shopService.toggleWishlist(member.getId(), id);
		return "redirect:/shop/" + id;
	}

	@PostMapping("/{id}/review")
	public String review(
			@PathVariable Long id,
			@AuthenticationPrincipal Member member,
			@RequestParam int rating,
			@RequestParam String content) {
		shopService.addReview(member.getId(), id, rating, content);
		return "redirect:/shop/" + id;
	}
}
