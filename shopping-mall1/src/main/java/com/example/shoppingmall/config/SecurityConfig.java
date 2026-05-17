package com.example.shoppingmall.config;

import com.example.shoppingmall.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 세션 기반 폼 로그인 + OAuth2(구글·네이버·카카오). 일반 사용자는 USER, 쇼핑몰 관리자는 ADMIN 역할을 사용합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final CustomOAuth2UserService customOAuth2UserService;

	public SecurityConfig(@Lazy CustomOAuth2UserService customOAuth2UserService) {
		this.customOAuth2UserService = customOAuth2UserService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(
								"/css/**",
								"/images/**",
								"/uploads/**",
								"/error",
								"/",
								"/login",
								"/register",
								"/oauth2/**",
								"/login/oauth2/**",
								"/products",
								"/products/**")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/cart/**", "/orders/**", "/mypage/**").authenticated()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.defaultSuccessUrl("/", true)
						.permitAll())
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/login")
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
						.defaultSuccessUrl("/", true))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll());
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
