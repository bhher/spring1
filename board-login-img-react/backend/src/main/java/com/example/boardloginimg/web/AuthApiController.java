package com.example.boardloginimg.web;

import com.example.boardloginimg.domain.User;
import com.example.boardloginimg.service.UserService;
import com.example.boardloginimg.web.dto.LoginRequest;
import com.example.boardloginimg.web.dto.RegisterApiRequest;
import com.example.boardloginimg.web.dto.SessionUserResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    public static final String SESSION_USER = "loginUser";

    private final UserService userService;

    public AuthApiController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<SessionUserResponse> login(@Valid @RequestBody LoginRequest body, HttpSession session) {
        User user = userService.login(body.username().trim(), body.password());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        session.setAttribute(SESSION_USER, user.getUsername());
        session.setAttribute("loginName", user.getName());
        return ResponseEntity.ok(new SessionUserResponse(user.getUsername(), user.getName()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterApiRequest body) {
        if (!body.password().equals(body.passwordConfirm())) {
            return ResponseEntity.badRequest().body("비밀번호 확인이 일치하지 않습니다.");
        }
        try {
            userService.register(body.username(), body.password(), body.name());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/me")
    public ResponseEntity<SessionUserResponse> me(HttpSession session) {
        String username = (String) session.getAttribute(SESSION_USER);
        if (username == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String name = (String) session.getAttribute("loginName");
        return ResponseEntity.ok(new SessionUserResponse(username, name != null ? name : ""));
    }
}
