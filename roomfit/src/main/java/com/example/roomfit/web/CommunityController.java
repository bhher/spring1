package com.example.roomfit.web;

import com.example.roomfit.domain.CommunityBoardType;
import com.example.roomfit.domain.Member;
import com.example.roomfit.service.CommunityService;
import com.example.roomfit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {

	private final CommunityService communityService;
	private final ReportService reportService;

	@GetMapping
	public String list(
			@RequestParam(defaultValue = "FREE") CommunityBoardType board,
			@RequestParam(defaultValue = "0") int page,
			Model model) {
		model.addAttribute("board", board);
		model.addAttribute("boards", CommunityBoardType.values());
		model.addAttribute("page", communityService.list(board, PageRequest.of(page, 15)));
		return "community/list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("post", communityService.getDetail(id));
		return "community/detail";
	}

	@GetMapping("/write")
	public String writeForm(@RequestParam CommunityBoardType board, Model model) {
		model.addAttribute("board", board);
		return "community/form";
	}

	@PostMapping("/write")
	public String write(
			@AuthenticationPrincipal Member member,
			@RequestParam CommunityBoardType board,
			@RequestParam String title,
			@RequestParam String content) {
		Long id = communityService.create(member.getId(), board, title, content);
		return "redirect:/community/" + id;
	}

	@PostMapping("/report")
	public String report(
			@AuthenticationPrincipal Member member,
			@RequestParam String targetType,
			@RequestParam Long targetId,
			@RequestParam String reason) {
		reportService.report(member.getId(), targetType, targetId, reason);
		return "redirect:/community";
	}
}
