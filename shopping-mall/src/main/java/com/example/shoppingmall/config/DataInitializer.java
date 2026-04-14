package com.example.shoppingmall.config;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.domain.Role;
import com.example.shoppingmall.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 최초 실행 시 관리자 계정이 없으면 기본 ADMIN 계정을 만듭니다. (개발·데모용)
 */
@Configuration
@Profile("!test")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

	private final MemberRepository memberRepository;
	private final PasswordEncoder passwordEncoder;

	@Bean
	CommandLineRunner seedAdmin() {
		return args -> {
			String email = "admin@shopping.local";
			if (memberRepository.existsByEmail(email)) {
				return;
			}
			Member admin = Member.builder()
					.email(email)
					.password(passwordEncoder.encode("admin1234"))
					.name("관리자")
					.role(Role.ADMIN)
					.build();
			memberRepository.save(admin);
			log.info("기본 관리자 계정 생성: {} / 비밀번호: admin1234", email);
		};
	}
}
