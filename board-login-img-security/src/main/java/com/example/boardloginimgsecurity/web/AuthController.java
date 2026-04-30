package com.example.boardloginimgsecurity.web;

import com.example.boardloginimgsecurity.security.SecurityUtils;
import com.example.boardloginimgsecurity.service.UserService;
import com.example.boardloginimgsecurity.web.dto.RegisterForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String loginForm() {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/home";
        }
        return "login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (SecurityUtils.isAuthenticated()) {
            return "redirect:/home";
        }
        model.addAttribute("form", new RegisterForm());
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("form") RegisterForm form,
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
            userService.register(form.getUsername(), form.getPassword(), form.getName());
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
        return "redirect:/login?registered";
    }
}
