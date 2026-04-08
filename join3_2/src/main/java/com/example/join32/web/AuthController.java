package com.example.join32.web;

import com.example.join32.dto.LoginRequest;
import com.example.join32.dto.UserRegisterRequest;
import com.example.join32.dto.UserResponse;
import com.example.join32.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    public static final String SESSION_USER = "loginUser";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        if (session.getAttribute(SESSION_USER) != null) {
            return "redirect:/home";
        }
        model.addAttribute("login", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String login(
            @Valid @ModelAttribute("login") LoginRequest loginRequest,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "login";
        }
        Optional<UserResponse> userOpt = userService.login(loginRequest);
        if (userOpt.isEmpty()) {
            LoginRequest retry = new LoginRequest();
            retry.setUsername(loginRequest.getUsername());
            model.addAttribute("error", "아이디 또는 비밀번호가 올바르지 않습니다.");
            model.addAttribute("login", retry);
            return "login";
        }
        UserResponse user = userOpt.get();
        session.setAttribute(SESSION_USER, user.username());
        session.setAttribute("loginName", user.name());
        return "redirect:/home";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String registerForm(HttpSession session, Model model) {
        if (session.getAttribute(SESSION_USER) != null) {
            return "redirect:/home";
        }
        model.addAttribute("form", new UserRegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("form") UserRegisterRequest form,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        if (!form.getPassword().equals(form.getPasswordConfirm())) {
            model.addAttribute("error", "비밀번호 확인이 일치하지 않습니다.");
            return "register";
        }
        try {
            userService.register(form);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
