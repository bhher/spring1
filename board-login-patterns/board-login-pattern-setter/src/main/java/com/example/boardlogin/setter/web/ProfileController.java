package com.example.boardlogin.setter.web;

import com.example.boardlogin.setter.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        String username = (String) session.getAttribute(AuthController.SESSION_USER);
        model.addAttribute("userProfile", userService.loadProfile(username));
        return "profile";
    }
}
