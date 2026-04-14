package com.example.shoppingmall.web;

import com.example.shoppingmall.dto.MemberRegisterDto;
import com.example.shoppingmall.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * 회원가입 및 로그인 페이지 (로그인 처리는 Spring Security 폼 로그인).
 */
@Controller
@RequiredArgsConstructor
public class AuthController {

	private final MemberService memberService;

	@GetMapping("/login")
	public String login() {
		return "auth/login";
	}

	@GetMapping("/register")
	public String registerForm(@ModelAttribute("form") MemberRegisterDto form) {
		return "auth/register";
	}

	@PostMapping("/register")
	public String register(
			@Valid @ModelAttribute("form") MemberRegisterDto form,
			BindingResult bindingResult,
			RedirectAttributes ra) {
		if (bindingResult.hasErrors()) {
			return "auth/register";
		}
		memberService.register(form);
		ra.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해 주세요.");
		return "redirect:/login";
	}
}
