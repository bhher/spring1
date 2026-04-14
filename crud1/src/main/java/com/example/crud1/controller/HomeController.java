package com.example.crud1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 루트 URL에서 목록으로 안내하는 홈 화면.
 */
@Controller
public class HomeController {

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("pageTitle", "홈");
		return "index";
	}
}
