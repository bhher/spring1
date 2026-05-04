package com.example.boardloginimgsecurity1.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Thymeleaf 화면 라우트. 데이터는 {@code /api/**} REST + 순수 JS(fetch)로 처리합니다.
 */
@Controller
public class PageController {

    @GetMapping("/")
    public String root() {
        return "redirect:/ui/posts";
    }

    @GetMapping("/ui/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/ui/register")
    public String registerPage() {
        return "auth/register";
    }

    @GetMapping("/ui/posts/write")
    public String writePage() {
        return "board/write";
    }

    @GetMapping("/ui/posts/{id:\\d+}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        model.addAttribute("postId", id);
        return "board/edit";
    }

    @GetMapping("/ui/posts/{id:\\d+}")
    public String detailPage(@PathVariable Long id, Model model) {
        model.addAttribute("postId", id);
        return "board/detail";
    }

    @GetMapping("/ui/posts")
    public String listPage() {
        return "board/list";
    }
}
