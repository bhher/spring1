package com.example.roomfit.web;

import com.example.roomfit.domain.Gender;
import com.example.roomfit.domain.Member;
import com.example.roomfit.dto.ProfileFormDto;
import com.example.roomfit.service.MemberService;
import com.example.roomfit.service.UserProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

	private final MemberService memberService;
	private final UserProfileService userProfileService;

	@GetMapping("/find-id")
	public String findIdForm() {
		return "member/find-id";
	}

	@PostMapping("/find-id")
	public String findId(@RequestParam("name") String name, @RequestParam("email") String email, Model model) {
		try {
			model.addAttribute("foundLoginId", memberService.findLoginId(name, email));
		} catch (Exception e) {
			model.addAttribute("error", e.getMessage());
		}
		return "member/find-id";
	}

	@GetMapping("/find-pw")
	public String findPwForm() {
		return "member/find-pw";
	}

	@PostMapping("/find-pw")
	public String findPw(
			@RequestParam("loginId") String loginId,
			@RequestParam("email") String email,
			@RequestParam("newPassword") String newPassword,
			RedirectAttributes ra) {
		try {
			memberService.resetPassword(loginId, email, newPassword);
			ra.addFlashAttribute("message", "비밀번호가 변경되었습니다. 로그인해 주세요.");
			return "redirect:/login";
		} catch (Exception e) {
			ra.addFlashAttribute("error", e.getMessage());
			return "redirect:/member/find-pw";
		}
	}

	@GetMapping("/mypage")
	public String mypage(@AuthenticationPrincipal Member member, Model model) {
		model.addAttribute("profile", userProfileService.findByMemberId(member.getId()));
		return "member/mypage";
	}

	@GetMapping("/edit")
	public String editForm(@AuthenticationPrincipal Member member, Model model) {
		model.addAttribute("member", memberService.findById(member.getId()));
		model.addAttribute("genders", Gender.values());
		return "member/edit";
	}

	@PostMapping("/edit")
	public String edit(
			@AuthenticationPrincipal Member member,
			@RequestParam("nickname") String nickname,
			@RequestParam(name = "phone", required = false) String phone,
			@RequestParam(name = "gender", required = false) Gender gender,
			RedirectAttributes ra) {
		memberService.updateMember(member.getId(), nickname, phone, gender);
		ra.addFlashAttribute("message", "회원정보가 수정되었습니다.");
		return "redirect:/member/mypage";
	}

	@PostMapping("/withdraw")
	public String withdraw(@AuthenticationPrincipal Member member, RedirectAttributes ra) {
		memberService.withdraw(member.getId());
		ra.addFlashAttribute("message", "회원 탈퇴가 완료되었습니다.");
		return "redirect:/logout";
	}

	@GetMapping("/profile")
	public String profileForm(@AuthenticationPrincipal Member member, Model model) {
		var profile = userProfileService.findByMemberId(member.getId());
		if (profile != null) {
			ProfileFormDto dto = new ProfileFormDto();
			dto.setRoomSize(profile.getRoomSize());
			dto.setBudget(profile.getBudget());
			dto.setPreferredStyle(profile.getPreferredStyle());
			dto.setLifestyle(profile.getLifestyle());
			dto.setHasFurniture(profile.isHasFurniture());
			dto.setSleepPattern(profile.getSleepPattern());
			model.addAttribute("form", dto);
		} else {
			model.addAttribute("form", new ProfileFormDto());
		}
		return "member/profile";
	}

	@PostMapping("/profile")
	public String saveProfile(
			@AuthenticationPrincipal Member member,
			@Valid @ModelAttribute("form") ProfileFormDto form,
			BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return "member/profile";
		}
		userProfileService.saveOrUpdate(member.getId(), form);
		return "redirect:/recommend/result";
	}
}
