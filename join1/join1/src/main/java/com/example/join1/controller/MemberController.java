package com.example.join1.controller;

import com.example.join1.dto.SignupForm;
import com.example.join1.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

	private final MemberService memberService;

	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

	@GetMapping("/signup")
	public String signupForm(@ModelAttribute("signupForm") SignupForm signupForm) {
		return "signup";
	}

	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute("signupForm") SignupForm signupForm,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			return "signup";
		}
		if (!signupForm.getPassword().equals(signupForm.getPasswordConfirm())) {
			model.addAttribute("errorMessage", "비밀번호가 일치하지 않습니다.");
			return "signup";
		}
		try {
			memberService.register(signupForm.getUsername(), signupForm.getPassword(), signupForm.getEmail());
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "signup";
		}
		return "redirect:/login?registered";
	}
}
