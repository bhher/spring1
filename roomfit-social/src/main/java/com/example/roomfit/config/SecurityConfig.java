package com.example.roomfit.config;

import com.example.roomfit.security.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
								"/js/**",
								"/images/**",
								"/uploads/**",
								"/h2-console/**",
								"/error",
								"/",
								"/login",
								"/register",
								"/member/find-id",
								"/member/find-pw",
								"/oauth2/**",
								"/login/oauth2/**",
								"/interior",
								"/interior/**",
								"/community",
								"/community/**",
								"/shop",
								"/shop/**")
						.permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers(
								"/recommend/**",
								"/member/**",
								"/interior/write",
								"/interior/*/like",
								"/interior/*/comment",
								"/interior/*/delete",
								"/shop/*/cart",
								"/shop/cart",
								"/shop/*/wish",
								"/shop/*/review",
								"/community/write",
								"/community/report")
						.authenticated()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.usernameParameter("loginId")
						.defaultSuccessUrl("/", true)
						.permitAll())
				.oauth2Login(oauth2 -> oauth2
						.loginPage("/login")
						.defaultSuccessUrl("/", true)
						.userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService)))
				.logout(logout -> logout
						.logoutUrl("/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID")
						.permitAll())
				.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
				.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
