package com.example.boardlogin.mapper.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/posts";
    }

    @GetMapping("/home")
    public String home(HttpSession session) {
        if (session.getAttribute(AuthController.SESSION_USER) == null) {
            return "redirect:/login";
        }
        return "home";
    }
}
