package com.example.roomfit.web;

import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.domain.PostStatus;
import com.example.roomfit.recommend.RecommendEngine;
import com.example.roomfit.repository.InteriorPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final InteriorPostRepository interiorPostRepository;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("popular", interiorPostRepository
				.findTop20ByStatusOrderByLikeCountDescViewCountDescCreatedAtDesc(
						PostStatus.VISIBLE, PageRequest.of(0, 6)));
		model.addAttribute("latest", interiorPostRepository
				.findTop50ByStatusOrderByCreatedAtDesc(PostStatus.VISIBLE).stream().limit(6).toList());
		model.addAttribute("styles", InteriorStyle.values());
		return "index";
	}
}
