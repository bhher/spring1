package com.example.shoppingmall.service;

import com.example.shoppingmall.domain.Cart;
import com.example.shoppingmall.domain.CartItem;
import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.domain.Product;
import com.example.shoppingmall.domain.ProductImage;
import com.example.shoppingmall.dto.CartLineDto;
import com.example.shoppingmall.exception.BusinessException;
import com.example.shoppingmall.exception.ResourceNotFoundException;
import com.example.shoppingmall.repository.CartItemRepository;
import com.example.shoppingmall.repository.CartRepository;
import com.example.shoppingmall.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final ProductRepository productRepository;

	@Transactional
	public Cart getOrCreateCart(Member member) {
		return cartRepository.findByMember_Id(member.getId()).orElseGet(() -> {
			Cart c = Cart.builder().member(member).build();
			return cartRepository.save(c);
		});
	}

	@Transactional
	public void addItem(Member member, Long productId, int quantity) {
		if (quantity <= 0) {
			throw new BusinessException("수량은 1 이상이어야 합니다.");
		}
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
		if (product.getStockQuantity() < quantity) {
			throw new BusinessException("재고가 부족합니다.");
		}
		Cart cart = getOrCreateCart(member);
		Cart full = cartRepository.findWithItemsByMember_Id(member.getId()).orElse(cart);

		CartItem item = cartItemRepository.findByCartIdAndProductId(full.getId(), productId).orElse(null);
		if (item == null) {
			item = CartItem.builder().product(product).quantity(quantity).build();
			full.addItem(item);
		}
		else {
			int next = item.getQuantity() + quantity;
			if (product.getStockQuantity() < next) {
				throw new BusinessException("장바구니 수량이 재고를 초과합니다.");
			}
			item.setQuantity(next);
		}
		cartRepository.save(full);
	}

	@Transactional(readOnly = true)
	public List<CartLineDto> getCartLines(Member member) {
		Cart cart = cartRepository.findWithItemsByMember_Id(member.getId()).orElse(null);
		if (cart == null || cart.getItems().isEmpty()) {
			return List.of();
		}
		return cart.getItems().stream().map(this::toLine).toList();
	}

	@Transactional
	public void updateQuantity(Member member, Long cartItemId, int quantity) {
		if (quantity <= 0) {
			throw new BusinessException("수량은 1 이상이어야 합니다.");
		}
		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("장바구니 항목이 없습니다."));
		if (!item.getCart().getMember().getId().equals(member.getId())) {
			throw new BusinessException("본인 장바구니만 수정할 수 있습니다.");
		}
		Product p = item.getProduct();
		if (p.getStockQuantity() < quantity) {
			throw new BusinessException("재고가 부족합니다.");
		}
		item.setQuantity(quantity);
	}

	@Transactional
	public void removeItem(Member member, Long cartItemId) {
		CartItem item = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new ResourceNotFoundException("장바구니 항목이 없습니다."));
		if (!item.getCart().getMember().getId().equals(member.getId())) {
			throw new BusinessException("본인 장바구니만 수정할 수 있습니다.");
		}
		cartItemRepository.delete(item);
	}

	private CartLineDto toLine(CartItem item) {
		Product p = item.getProduct();
		BigDecimal line = p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
		String thumb = p.getImages().stream()
				.min(Comparator.comparing(ProductImage::getSortOrder))
				.map(ProductImage::getThumbnailUrlPath)
				.orElse("/images/no-image.svg");
		return new CartLineDto(
				item.getId(),
				p.getId(),
				p.getName(),
				p.getPrice(),
				item.getQuantity(),
				line,
				thumb);
	}
}
