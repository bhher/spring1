package com.example.roomfit.web;

import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.service.InteriorPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

	private final InteriorPostService interiorPostService;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("popular", interiorPostService.listPopularForView(6));
		model.addAttribute("latest", interiorPostService.listRecentForView(6));
		model.addAttribute("styles", InteriorStyle.values());
		return "index";
	}
}
