package com.example.shoppingmall.web.api;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.dto.CartAddRequest;
import com.example.shoppingmall.dto.CartLineDto;
import com.example.shoppingmall.dto.CartQuantityRequest;
import com.example.shoppingmall.service.CartService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class ApiCartController {

	private final CartService cartService;

	@GetMapping
	public List<CartLineDto> get(@AuthenticationPrincipal Member member) {
		return cartService.getCartLines(member);
	}

	@PostMapping("/items")
	public ResponseEntity<Map<String, String>> add(
			@AuthenticationPrincipal Member member,
			@Valid @RequestBody CartAddRequest body) {
		cartService.addItem(member, body.productId(), body.quantity());
		return ResponseEntity.ok(Map.of("message", "장바구니에 담았습니다."));
	}

	@PutMapping("/items/{itemId}/quantity")
	public ResponseEntity<Void> updateQuantity(
			@AuthenticationPrincipal Member member,
			@PathVariable Long itemId,
			@Valid @RequestBody CartQuantityRequest body) {
		cartService.updateQuantity(member, itemId, body.quantity());
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/items/{itemId}")
	public ResponseEntity<Map<String, String>> remove(
			@AuthenticationPrincipal Member member,
			@PathVariable Long itemId) {
		cartService.removeItem(member, itemId);
		return ResponseEntity.ok(Map.of("message", "삭제했습니다."));
	}
}
