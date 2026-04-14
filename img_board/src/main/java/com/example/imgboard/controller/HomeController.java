package com.example.imgboard.controller;

import com.example.imgboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final PostService postService;

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("posts", postService.findAll());
		return "index";
	}
}
