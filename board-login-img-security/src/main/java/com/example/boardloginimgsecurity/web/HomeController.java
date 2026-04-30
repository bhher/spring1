package com.example.boardloginimgsecurity.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "redirect:/posts";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }
}
