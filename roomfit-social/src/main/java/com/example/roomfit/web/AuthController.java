package com.example.roomfit.web;

import com.example.roomfit.dto.MemberRegisterDto;
import com.example.roomfit.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
		try {
			memberService.register(form);
		} catch (Exception e) {
			bindingResult.reject("register.fail", e.getMessage());
			return "auth/register";
		}
		ra.addFlashAttribute("message", "회원가입이 완료되었습니다.");
		return "redirect:/login";
	}
}
