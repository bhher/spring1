package com.example.boardloginimgsecurityoauth.config;

import com.example.boardloginimgsecurityoauth.security.CustomOAuth2UserService;
import com.example.boardloginimgsecurityoauth.security.CustomOidcUserService;
import com.example.boardloginimgsecurityoauth.security.LoginUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserService customOidcUserService;

    public SecurityConfig(
            @Lazy CustomOAuth2UserService customOAuth2UserService,
            @Lazy CustomOidcUserService customOidcUserService) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserService = customOidcUserService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            LoginUserDetailsService loginUserDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(loginUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider)
            throws Exception {
        http.authenticationProvider(authenticationProvider);
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/uploads/**", "/error").permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/logout").permitAll()
                .requestMatchers(HttpMethod.POST, "/register").permitAll()
                .requestMatchers("/posts/write").authenticated()
                .requestMatchers("/posts/*/edit").authenticated()
                .requestMatchers(HttpMethod.POST, "/posts/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/posts").permitAll()
                .requestMatchers(HttpMethod.GET, "/posts/**").permitAll()
                .requestMatchers("/home").authenticated()
                .anyRequest().authenticated());
        http.formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error")
                .permitAll());
        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(customOAuth2UserService)
                        .oidcUserService(customOidcUserService)));
        http.logout(logout -> logout
                .logoutRequestMatcher(PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/logout"))
                .logoutSuccessUrl("/posts"));
        return http.build();
    }
}
