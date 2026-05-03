package com.example.memberboard.domain.user;

import com.example.memberboard.config.SessionConst;
import com.example.memberboard.domain.user.dto.UserLoginDto;
import com.example.memberboard.domain.user.dto.UserRegisterDto;
import com.example.memberboard.domain.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@GetMapping("/signup")
	public String signupForm(@ModelAttribute("form") UserRegisterDto form) {
		return "user/signup";
	}

	@PostMapping("/signup")
	public String signup(@Valid @ModelAttribute("form") UserRegisterDto form,
			BindingResult bindingResult,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "user/signup";
		}
		try {
			userService.register(form);
		} catch (IllegalArgumentException ex) {
			bindingResult.reject("global", ex.getMessage());
			return "user/signup";
		}
		redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다. 로그인해 주세요.");
		return "redirect:/login";
	}

	@GetMapping("/login")
	public String loginForm(@ModelAttribute("form") UserLoginDto form,
			@RequestParam(value = "redirect", required = false) String redirect,
			Model model) {
		model.addAttribute("redirect", redirect);
		return "user/login";
	}

	@PostMapping("/login")
	public String login(@Valid @ModelAttribute("form") UserLoginDto form,
			BindingResult bindingResult,
			@RequestParam(value = "redirect", required = false) String redirect,
			HttpSession session,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("redirect", redirect);
			return "user/login";
		}
		try {
			Long userId = userService.login(form);
			session.setAttribute(SessionConst.LOGIN_USER_ID, userId);
		} catch (IllegalArgumentException ex) {
			bindingResult.reject("global", ex.getMessage());
			model.addAttribute("redirect", redirect);
			return "user/login";
		}
		if (redirect != null && !redirect.isBlank()) {
			return "redirect:" + redirect;
		}
		return "redirect:/boards";
	}

	@PostMapping("/logout")
	public String logout(HttpSession session) {
		userService.logout(session);
		return "redirect:/";
	}

	@GetMapping("/me")
	public String me(HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
		UserResponseDto profile = userService.getProfile(userId);
		model.addAttribute("profile", profile);
		return "user/me";
	}
}
