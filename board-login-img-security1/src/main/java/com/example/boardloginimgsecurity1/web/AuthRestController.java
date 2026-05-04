package com.example.boardloginimgsecurity1.web;

import com.example.boardloginimgsecurity1.security.JwtService;
import com.example.boardloginimgsecurity1.security.LoginUserDetails;
import com.example.boardloginimgsecurity1.service.UserService;
import com.example.boardloginimgsecurity1.web.dto.LoginRequest;
import com.example.boardloginimgsecurity1.web.dto.RegisterRequest;
import com.example.boardloginimgsecurity1.web.dto.TokenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    public AuthRestController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        LoginUserDetails user = (LoginUserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new TokenResponse(token, "Bearer", user.getUsername(), user.getDisplayName()));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new IllegalArgumentException("비밀번호 확인이 일치하지 않습니다.");
        }
        userService.register(request.getUsername(), request.getPassword(), request.getName());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
