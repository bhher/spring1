package com.example.shoppingmall.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * 세션 + 폼 로그인. 화면은 React SPA, API는 {@code /api/**} JSON.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final ApiAuthenticationEntryPoint apiAuthenticationEntryPoint;
	private final ApiAccessDeniedHandler apiAccessDeniedHandler;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(cors -> {})
				.csrf(csrf -> csrf
						.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint(apiAuthenticationEntryPoint)
						.accessDeniedHandler(apiAccessDeniedHandler))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(
								"/css/**",
								"/images/**",
								"/uploads/**",
								"/assets/**",
								"/vite.svg",
								"/error")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/", "/index.html").permitAll()
						.requestMatchers(HttpMethod.GET, "/login", "/register").permitAll()
						.requestMatchers(HttpMethod.GET, "/products", "/products/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/products", "/api/products/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/auth/csrf").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login").permitAll()
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/cart/**", "/orders/**", "/mypage/**").authenticated()
						.requestMatchers("/api/admin/**").hasRole("ADMIN")
						.requestMatchers("/api/cart/**", "/api/orders/**", "/api/mypage/**").authenticated()
						.requestMatchers("/api/auth/me", "/api/auth/logout").authenticated()
						.anyRequest().authenticated())
				.formLogin(form -> form
						.loginPage("/login")
						.loginProcessingUrl("/api/auth/login")
						.usernameParameter("username")
						.passwordParameter("password")
						.defaultSuccessUrl("/", true)
						.permitAll())
				.logout(logout -> logout
						.logoutUrl("/api/auth/logout")
						.logoutSuccessUrl("/")
						.invalidateHttpSession(true)
						.deleteCookies("JSESSIONID", "XSRF-TOKEN")
						.permitAll());
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
