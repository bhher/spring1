package com.example.board1.controller;

import com.example.board1.domain.Post;
import com.example.board1.service.PostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/posts")
public class PostController {

	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping
	public String list(
			@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
			Model model) {
		model.addAttribute("pageTitle", "게시글 목록");
		model.addAttribute("page", postService.findAll(pageable));
		return "posts/list";
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		Post post = postService.findById(id);
		model.addAttribute("post", post);
		model.addAttribute("pageTitle", post.getTitle() + " - 게시판");
		return "posts/detail";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("post", new Post());
		model.addAttribute("pageTitle", "글쓰기");
		return "posts/form";
	}

	@PostMapping
	public String create(
			@Valid @ModelAttribute("post") Post post,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "글쓰기");
			return "posts/form";
		}
		postService.create(post);
		return "redirect:/posts";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		model.addAttribute("post", postService.findById(id));
		model.addAttribute("pageTitle", "글 수정");
		return "posts/form";
	}

	@PostMapping("/{id}")
	public String update(
			@PathVariable Long id,
			@Valid @ModelAttribute("post") Post post,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			post.setId(id);
			model.addAttribute("pageTitle", "글 수정");
			return "posts/form";
		}
		postService.update(id, post);
		return "redirect:/posts/" + id;
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
		postService.delete(id);
		redirectAttributes.addFlashAttribute("message", "삭제되었습니다.");
		return "redirect:/posts";
	}
}
