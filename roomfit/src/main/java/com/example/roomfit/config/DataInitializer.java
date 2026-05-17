package com.example.roomfit.config;

import com.example.roomfit.domain.InteriorPost;
import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.PostImage;
import com.example.roomfit.domain.Product;
import com.example.roomfit.domain.Role;
import com.example.roomfit.domain.UserProfile;
import com.example.roomfit.repository.InteriorPostRepository;
import com.example.roomfit.repository.MemberRepository;
import com.example.roomfit.repository.ProductRepository;
import com.example.roomfit.repository.UserProfileRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

	private final MemberRepository memberRepository;
	private final UserProfileRepository userProfileRepository;
	private final InteriorPostRepository interiorPostRepository;
	private final ProductRepository productRepository;
	private final PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner seed() {
		return args -> seedAdmin();
	}

	private void seedAdmin() {
		if (memberRepository.existsByLoginId("admin")) {
			return;
		}
		memberRepository.save(Member.builder()
				.loginId("admin")
				.password(passwordEncoder.encode("admin1234"))
				.name("관리자")
				.nickname("RoomFit관리자")
				.email("admin@roomfit.local")
				.role(Role.ADMIN)
				.build());
		log.info("관리자: admin / admin1234");

		Member user = memberRepository.save(Member.builder()
				.loginId("user1")
				.password(passwordEncoder.encode("user1234"))
				.name("김자취")
				.nickname("원룸러버")
				.email("user1@roomfit.local")
				.role(Role.USER)
				.build());
		log.info("데모 회원: user1 / user1234");

		userProfileRepository.save(UserProfile.builder()
				.member(user)
				.roomSize(BigDecimal.valueOf(8.5))
				.budget(150)
				.preferredStyle(InteriorStyle.MINIMAL)
				.lifestyle("재택")
				.hasFurniture(false)
				.sleepPattern("아침형")
				.build());

		InteriorPost post = InteriorPost.builder()
				.author(user)
				.style(InteriorStyle.MINIMAL)
				.title("8평 원룸 미니멀 자취방")
				.content("화이트 톤과 수납 침대로 공간을 넓게 썼습니다.")
				.roomSize(BigDecimal.valueOf(8.5))
				.budget(120)
				.likeCount(12)
				.viewCount(80)
				.build();
		post.addImage(PostImage.builder()
				.filePath("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800")
				.thumbnail(true)
				.build());
		interiorPostRepository.save(post);

		productRepository.save(Product.builder()
				.name("미니 수납 협탁")
				.price(39000)
				.styleTag(InteriorStyle.MINIMAL)
				.imagePath("https://images.unsplash.com/photo-1555041469-a586c61ea9bc?w=400")
				.avgRating(4.5)
				.reviewCount(3)
				.build());
		productRepository.save(Product.builder()
				.name("북유럽 원목 스탠드")
				.price(59000)
				.styleTag(InteriorStyle.SCANDINAVIAN)
				.imagePath("https://images.unsplash.com/photo-1507473885765-e6ed057f782c?w=400")
				.avgRating(4.2)
				.build());
	}
}
