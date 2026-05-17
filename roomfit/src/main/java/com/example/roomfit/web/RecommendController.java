package com.example.roomfit.web;

import com.example.roomfit.domain.Member;
import com.example.roomfit.service.RecommendService;
import com.example.roomfit.service.ShopService;
import com.example.roomfit.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/recommend")
@RequiredArgsConstructor
public class RecommendController {

	private final RecommendService recommendService;
	private final UserProfileService userProfileService;
	private final ShopService shopService;

	@GetMapping
	public String survey(@AuthenticationPrincipal Member member) {
		if (userProfileService.findByMemberId(member.getId()) == null) {
			return "redirect:/member/profile";
		}
		return "redirect:/recommend/result";
	}

	@GetMapping("/result")
	public String result(@AuthenticationPrincipal Member member, Model model) {
		if (userProfileService.findByMemberId(member.getId()) == null) {
			return "redirect:/member/profile";
		}
		model.addAttribute("result", recommendService.getRecommendations(member.getId()));
		model.addAttribute("products", shopService.recommendProducts(member.getId()));
		return "recommend/result";
	}
}
