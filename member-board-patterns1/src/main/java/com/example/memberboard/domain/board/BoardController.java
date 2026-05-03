package com.example.memberboard.domain.board;

import com.example.memberboard.config.SessionConst;
import com.example.memberboard.domain.board.dto.BoardResponseDto;
import com.example.memberboard.domain.board.dto.BoardWriteDto;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	@GetMapping("/boards")
	public String list(Model model) {
		model.addAttribute("boards", boardService.findAll());
		return "board/list";
	}

	@GetMapping("/boards/{id}")
	public String detail(@PathVariable Long id, Model model) {
		try {
			model.addAttribute("board", boardService.findOne(id));
		} catch (IllegalArgumentException ex) {
			model.addAttribute("error", ex.getMessage());
			return "error/simple";
		}
		return "board/detail";
	}

	@GetMapping("/boards/new")
	public String newForm(@ModelAttribute("form") BoardWriteDto form) {
		return "board/form";
	}

	@PostMapping("/boards/create")
	public String create(@Valid @ModelAttribute("form") BoardWriteDto form,
			BindingResult bindingResult,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			return "board/form";
		}
		Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
		Long id = boardService.create(userId, form);
		redirectAttributes.addFlashAttribute("message", "글이 등록되었습니다.");
		return "redirect:/boards/" + id;
	}

	@GetMapping("/boards/{id}/edit")
	public String editForm(@PathVariable Long id, HttpSession session, Model model) {
		Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
		BoardResponseDto board;
		try {
			board = boardService.findOne(id);
		} catch (IllegalArgumentException ex) {
			model.addAttribute("error", ex.getMessage());
			return "error/simple";
		}
		if (!board.getAuthorId().equals(userId)) {
			model.addAttribute("error", "작성자만 수정할 수 있습니다.");
			return "error/simple";
		}
		BoardWriteDto form = new BoardWriteDto();
		form.setTitle(board.getTitle());
		form.setContent(board.getContent());
		model.addAttribute("boardId", id);
		model.addAttribute("form", form);
		return "board/edit";
	}

	@PostMapping("/boards/{id}/edit")
	public String edit(@PathVariable Long id,
			@Valid @ModelAttribute("form") BoardWriteDto form,
			BindingResult bindingResult,
			HttpSession session,
			Model model,
			RedirectAttributes redirectAttributes) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("boardId", id);
			return "board/edit";
		}
		Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
		try {
			boardService.update(id, userId, form);
		} catch (IllegalArgumentException | IllegalStateException ex) {
			bindingResult.reject("global", ex.getMessage());
			model.addAttribute("boardId", id);
			return "board/edit";
		}
		redirectAttributes.addFlashAttribute("message", "수정되었습니다.");
		return "redirect:/boards/" + id;
	}

	@PostMapping("/boards/{id}/delete")
	public String delete(@PathVariable Long id,
			HttpSession session,
			RedirectAttributes redirectAttributes) {
		Long userId = (Long) session.getAttribute(SessionConst.LOGIN_USER_ID);
		try {
			boardService.delete(id, userId);
		} catch (IllegalArgumentException | IllegalStateException ex) {
			redirectAttributes.addFlashAttribute("error", ex.getMessage());
			return "redirect:/boards/" + id;
		}
		redirectAttributes.addFlashAttribute("message", "삭제되었습니다.");
		return "redirect:/boards";
	}
}
