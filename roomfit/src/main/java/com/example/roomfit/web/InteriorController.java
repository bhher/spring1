package com.example.roomfit.web;

import com.example.roomfit.domain.InteriorStyle;
import com.example.roomfit.domain.Member;
import com.example.roomfit.dto.InteriorPostFormDto;
import com.example.roomfit.service.InteriorPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/interior")
@RequiredArgsConstructor
public class InteriorController {

	private final InteriorPostService interiorPostService;

	@GetMapping
	public String list(
			@RequestParam(required = false) InteriorStyle style,
			@RequestParam(defaultValue = "0") int page,
			Model model) {
		var pageable = PageRequest.of(page, 12);
		model.addAttribute("page", interiorPostService.list(style, pageable));
		model.addAttribute("style", style);
		model.addAttribute("styles", InteriorStyle.values());
		return "interior/list";
	}

	@GetMapping("/{id}")
	public String detail(
			@PathVariable Long id,
			@AuthenticationPrincipal Member member,
			Model model) {
		Long memberId = member != null ? member.getId() : null;
		var post = interiorPostService.getDetail(id, memberId);
		model.addAttribute("post", post);
		model.addAttribute("comments", interiorPostService.getComments(id));
		model.addAttribute("liked", interiorPostService.isLiked(id, memberId));
		return "interior/detail";
	}

	@GetMapping("/write")
	public String writeForm(Model model) {
		model.addAttribute("form", new InteriorPostFormDto());
		model.addAttribute("styles", InteriorStyle.values());
		return "interior/form";
	}

	@PostMapping("/write")
	public String write(
			@AuthenticationPrincipal Member member,
			@Valid @ModelAttribute("form") InteriorPostFormDto form,
			BindingResult bindingResult,
			@RequestParam(required = false) MultipartFile image,
			Model model) throws Exception {
		if (bindingResult.hasErrors()) {
			model.addAttribute("styles", InteriorStyle.values());
			return "interior/form";
		}
		Long id = interiorPostService.create(member.getId(), form, image);
		return "redirect:/interior/" + id;
	}

	@PostMapping("/{id}/like")
	public String like(@PathVariable Long id, @AuthenticationPrincipal Member member) {
		interiorPostService.toggleLike(id, member.getId());
		return "redirect:/interior/" + id;
	}

	@PostMapping("/{id}/comment")
	public String comment(
			@PathVariable Long id,
			@AuthenticationPrincipal Member member,
			@RequestParam String content,
			@RequestParam(required = false) Long parentId) {
		interiorPostService.addComment(id, member.getId(), content, parentId);
		return "redirect:/interior/" + id;
	}

	@PostMapping("/{id}/delete")
	public String delete(
			@PathVariable Long id,
			@AuthenticationPrincipal Member member,
			RedirectAttributes ra) {
		interiorPostService.delete(id, member.getId());
		ra.addFlashAttribute("message", "삭제되었습니다.");
		return "redirect:/interior";
	}
}
