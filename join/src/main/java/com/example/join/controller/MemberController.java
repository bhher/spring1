package com.example.join.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.join.dto.MemberRegisterDto;
import com.example.join.service.MemberService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;

	@GetMapping("/register")
	public String registerForm(Model model) {
		model.addAttribute("member", new MemberRegisterDto());
		return "register";
	}

	@PostMapping("/register")
	public String register(@ModelAttribute("member") MemberRegisterDto dto, Model model) {
		model.addAttribute("user", memberService.register(dto));
		return "welcome";
	}
}
