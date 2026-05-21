package com.example.roomfit.config;

import com.example.roomfit.domain.*;
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
		return args -> {
			seedMembers();
			seedInteriorPosts();
			seedProducts();
		};
	}

	private void seedMembers() {
		if (!memberRepository.existsByLoginId("admin")) {
			memberRepository.save(Member.builder()
					.loginId("admin")
					.password(passwordEncoder.encode("admin1234"))
					.name("관리자")
					.nickname("RoomFit관리자")
					.email("admin@roomfit.local")
					.role(Role.ADMIN)
					.build());
			log.info("관리자: admin / admin1234");
		}

		Member user1 = memberRepository.findByLoginId("user1").orElseGet(() -> {
			Member saved = memberRepository.save(Member.builder()
					.loginId("user1")
					.password(passwordEncoder.encode("user1234"))
					.name("김자취")
					.nickname("원룸러버")
					.email("user1@roomfit.local")
					.role(Role.USER)
					.build());
			log.info("데모 회원: user1 / user1234");
			return saved;
		});

		if (userProfileRepository.findByMemberId(user1.getId()).isEmpty()) {
			userProfileRepository.save(UserProfile.builder()
					.member(user1)
					.roomSize(BigDecimal.valueOf(8.5))
					.budget(150)
					.preferredStyle(InteriorStyle.MINIMAL)
					.lifestyle("재택")
					.hasFurniture(false)
					.sleepPattern("아침형")
					.build());
		}

		Member user2 = memberRepository.findByLoginId("user2").orElseGet(() -> {
			Member saved = memberRepository.save(Member.builder()
					.loginId("user2")
					.password(passwordEncoder.encode("user1234"))
					.name("이인테")
					.nickname("북유럽감성")
					.email("user2@roomfit.local")
					.role(Role.USER)
					.build());
			log.info("데모 회원: user2 / user1234");
			return saved;
		});

		if (userProfileRepository.findByMemberId(user2.getId()).isEmpty()) {
			userProfileRepository.save(UserProfile.builder()
					.member(user2)
					.roomSize(BigDecimal.valueOf(12.0))
					.budget(200)
					.preferredStyle(InteriorStyle.SCANDINAVIAN)
					.lifestyle("출퇴근")
					.hasFurniture(true)
					.sleepPattern("저녁형")
					.build());
		}
	}

	private void seedInteriorPosts() {
		if (interiorPostRepository.count() > 0) {
			return;
		}

		Member user1 = memberRepository.findByLoginId("user1").orElse(null);
		Member user2 = memberRepository.findByLoginId("user2").orElse(null);
		if (user1 == null || user2 == null) {
			log.warn("샘플 인테리어 글을 넣을 회원이 없어 건너뜁니다.");
			return;
		}

		savePost(user1, InteriorStyle.MINIMAL, "8평 원룸 미니멀 자취방",
				"화이트 톤과 수납 침대로 공간을 넓게 썼습니다.",
				BigDecimal.valueOf(8.5), 120, 12, 80,
				"https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=800");
		savePost(user1, InteriorStyle.BUDGET, "가성비 10평 원룸 꾸미기",
				"이케아 수납장과 중고 책상으로 15만 원 안에 마무리했습니다.",
				BigDecimal.valueOf(10.0), 150, 8, 45,
				"https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?w=800");
		savePost(user1, InteriorStyle.MODERN, "모던 화이트&그레이 거실",
				"그레이 소파와 화이트 벽으로 깔끔한 모던 무드를 연출했습니다.",
				BigDecimal.valueOf(9.0), 180, 15, 62,
				"https://images.unsplash.com/photo-1616486338812-3dadae4b4ace?w=800");
		savePost(user1, InteriorStyle.BEGINNER, "자취 첫날, 최소 비용으로 꾸미기",
				"침대·책상·의자만으로 시작한 1인실. 점점 채워가는 중입니다.",
				BigDecimal.valueOf(7.0), 80, 5, 30,
				"https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?w=800");
		savePost(user1, InteriorStyle.EMOTIONAL, "감성 조명 가득한 침실",
				"페어리라이트와 캔들로 따뜻한 분위기를 만들었습니다.",
				BigDecimal.valueOf(8.5), 100, 20, 95,
				"https://images.unsplash.com/photo-1513694203232-719a280e022f?w=800");

		savePost(user2, InteriorStyle.SCANDINAVIAN, "북유럽 톤 거실 인테리어",
				"원목 가구와 베이지 톤 패브릭으로 아늑한 거실을 완성했습니다.",
				BigDecimal.valueOf(12.0), 220, 18, 72,
				"https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=800");
		savePost(user2, InteriorStyle.MODERN, "12평 모던 스튜디오",
				"오픈형 주방과 거실을 일체형으로 연결해 공간감을 살렸습니다.",
				BigDecimal.valueOf(12.0), 250, 22, 88,
				"https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=800");
		savePost(user2, InteriorStyle.MINIMAL, "미니멀 원룸 수납 정리",
				"벽걸이 선반과 숨은 수납으로 물건을 최소화했습니다.",
				BigDecimal.valueOf(10.5), 160, 10, 55,
				"https://images.unsplash.com/photo-1493809842364-78817add7ffb?w=800");
		savePost(user2, InteriorStyle.EMOTIONAL, "라탄&플랜트 감성룸",
				"라탄 의자와 관엽식물로 자연스러운 감성 공간을 꾸몄습니다.",
				BigDecimal.valueOf(11.0), 190, 25, 110,
				"https://images.unsplash.com/photo-1618221195710-dd6b41faaea6?w=800");
		savePost(user2, InteriorStyle.BUDGET, "15만 원 이하 가성비 인테리어",
				"다이소·중고거래를 활용해 저예산으로 분위기를 바꿨습니다.",
				BigDecimal.valueOf(9.5), 150, 14, 67,
				"https://images.unsplash.com/photo-1554995207-c18c203602cb?w=800");

		log.info("샘플 인테리어 글 10건을 등록했습니다.");
	}

	private void savePost(Member author, InteriorStyle style, String title, String content,
			BigDecimal roomSize, int budget, int likeCount, int viewCount, String imageUrl) {
		InteriorPost post = InteriorPost.builder()
				.author(author)
				.style(style)
				.title(title)
				.content(content)
				.roomSize(roomSize)
				.budget(budget)
				.likeCount(likeCount)
				.viewCount(viewCount)
				.build();
		post.addImage(PostImage.builder()
				.filePath(imageUrl)
				.thumbnail(true)
				.build());
		interiorPostRepository.save(post);
	}

	private void seedProducts() {
		if (productRepository.count() > 0) {
			return;
		}
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
				.reviewCount(3)
				.build());
		productRepository.save(Product.builder()
				.name("라탄 사이드 테이블")
				.price(45000)
				.styleTag(InteriorStyle.EMOTIONAL)
				.imagePath("https://images.unsplash.com/photo-1615873968403-89e068629265?w=400")
				.avgRating(4.7)
				.reviewCount(5)
				.build());
		productRepository.save(Product.builder()
				.name("LED 무드등")
				.price(28000)
				.styleTag(InteriorStyle.MODERN)
				.imagePath("https://images.unsplash.com/photo-1513506003901-1e6a229e2d15?w=400")
				.avgRating(4.3)
				.reviewCount(8)
				.build());
		productRepository.save(Product.builder()
				.name("수납형 선반 유닛")
				.price(67000)
				.styleTag(InteriorStyle.BUDGET)
				.imagePath("https://images.unsplash.com/photo-1595428774223-ef52624120d2?w=400")
				.avgRating(4.0)
				.reviewCount(4)
				.build());
		log.info("샘플 상품 5건을 등록했습니다.");
	}
}
