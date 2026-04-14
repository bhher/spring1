package com.example.imgboard.controller;

import com.example.imgboard.dto.PostDto;
import com.example.imgboard.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {

	private final PostService postService;

	@GetMapping("/write")
	public String writeForm() {
		return "write";
	}

	@PostMapping("/write")
	public String write(PostDto dto) {
		Long id = postService.save(dto);
		return "redirect:/post/" + id;
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("post", postService.findById(id));
		return "detail";
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id) {
		postService.delete(id);
		return "redirect:/";
	}
}
