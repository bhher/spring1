package com.example.roomfit.service;

import com.example.roomfit.domain.Cart;
import com.example.roomfit.domain.CartItem;
import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.Product;
import com.example.roomfit.domain.ProductReview;
import com.example.roomfit.domain.UserProfile;
import com.example.roomfit.domain.Wishlist;
import com.example.roomfit.exception.BusinessException;
import com.example.roomfit.exception.ResourceNotFoundException;
import com.example.roomfit.repository.CartRepository;
import com.example.roomfit.repository.ProductRepository;
import com.example.roomfit.repository.ProductReviewRepository;
import com.example.roomfit.repository.WishlistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopService {

	private final ProductRepository productRepository;
	private final CartRepository cartRepository;
	private final WishlistRepository wishlistRepository;
	private final ProductReviewRepository productReviewRepository;
	private final MemberService memberService;
	private final UserProfileService userProfileService;

	public List<Product> listAll() {
		return productRepository.findByOnSaleTrueOrderByAvgRatingDesc();
	}

	public Product getProduct(Long id) {
		return productRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("상품을 찾을 수 없습니다."));
	}

	public List<Product> recommendProducts(Long memberId) {
		UserProfile profile = userProfileService.findByMemberId(memberId);
		if (profile == null) {
			return productRepository.findByOnSaleTrueOrderByAvgRatingDesc().stream().limit(8).toList();
		}
		int maxPrice = profile.getBudget() * 10000;
		return productRepository
				.findByOnSaleTrueAndStyleTagAndPriceLessThanEqualOrderByAvgRatingDesc(
						profile.getPreferredStyle(), maxPrice)
				.stream()
				.limit(8)
				.toList();
	}

	@Transactional
	public Cart getOrCreateCart(Long memberId) {
		return cartRepository.findByMemberId(memberId).orElseGet(() -> {
			Member member = memberService.findById(memberId);
			return cartRepository.save(Cart.builder().member(member).build());
		});
	}

	@Transactional
	public void addToCart(Long memberId, Long productId, int quantity) {
		Cart cart = getOrCreateCart(memberId);
		Product product = getProduct(productId);
		CartItem item = cart.getItems().stream()
				.filter(i -> i.getProduct().getId().equals(productId))
				.findFirst()
				.orElse(null);
		if (item == null) {
			cart.getItems().add(CartItem.builder().cart(cart).product(product).quantity(quantity).build());
		} else {
			item.setQuantity(item.getQuantity() + quantity);
		}
	}

	@Transactional
	public Cart getCartWithItems(Long memberId) {
		return cartRepository.findWithItemsByMemberId(memberId)
				.orElseGet(() -> getOrCreateCart(memberId));
	}

	@Transactional
	public void toggleWishlist(Long memberId, Long productId) {
		var existing = wishlistRepository.findByMemberIdAndProductId(memberId, productId);
		if (existing.isPresent()) {
			wishlistRepository.delete(existing.get());
			return;
		}
		Member member = memberService.findById(memberId);
		Product product = getProduct(productId);
		wishlistRepository.save(Wishlist.builder().member(member).product(product).build());
	}

	public boolean isWished(Long memberId, Long productId) {
		return wishlistRepository.existsByMemberIdAndProductId(memberId, productId);
	}

	@Transactional
	public void addReview(Long memberId, Long productId, int rating, String content) {
		if (rating < 1 || rating > 5) {
			throw new BusinessException("평점은 1~5 사이입니다.");
		}
		Product product = getProduct(productId);
		Member member = memberService.findById(memberId);
		if (productReviewRepository.findByProductIdAndMemberId(productId, memberId).isPresent()) {
			throw new BusinessException("이미 리뷰를 작성했습니다.");
		}
		productReviewRepository.save(ProductReview.builder()
				.product(product)
				.member(member)
				.rating(rating)
				.content(content)
				.build());
		recalculateRating(product);
	}

	private void recalculateRating(Product product) {
		List<ProductReview> reviews = productReviewRepository.findByProductIdOrderByCreatedAtDesc(product.getId());
		double avg = reviews.stream().mapToInt(ProductReview::getRating).average().orElse(0);
		product.setAvgRating(avg);
		product.setReviewCount(reviews.size());
	}

	public List<ProductReview> getReviews(Long productId) {
		return productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
	}
}
