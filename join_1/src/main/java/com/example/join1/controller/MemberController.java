package com.example.join1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.join1.dto.MemberRegisterDto;
import com.example.join1.dto.MemberSummaryDto;
import com.example.join1.service.MemberService;

@Controller
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("member", new MemberRegisterDto());
		return "register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute("member") MemberRegisterDto dto, RedirectAttributes redirectAttributes) {
		MemberSummaryDto summary = memberService.register(dto);
		redirectAttributes.addFlashAttribute("member", summary);
		return "redirect:/welcome";
	}

	@GetMapping("/welcome")
	public String welcome(@ModelAttribute("member") MemberSummaryDto member) {
		return "welcome";
	}
}
