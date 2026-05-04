package com.example.thymeleafexamples.web;

import com.example.thymeleafexamples.domain.Note;
import com.example.thymeleafexamples.service.NoteService;
import com.example.thymeleafexamples.web.dto.NoteForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notes")
public class NoteController {

	private final NoteService noteService;

	public NoteController(NoteService noteService) {
		this.noteService = noteService;
	}

	@GetMapping
	public String list(Model model) {
		model.addAttribute("pageTitle", "메모 목록");
		model.addAttribute("notes", noteService.findAll());
		return "notes/list";
	}

	@GetMapping("/new")
	public String newForm(Model model) {
		model.addAttribute("pageTitle", "메모 작성");
		model.addAttribute("noteForm", new NoteForm());
		return "notes/form";
	}

	@PostMapping
	public String create(@Valid @ModelAttribute("noteForm") NoteForm form, BindingResult bindingResult, Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "메모 작성");
			return "notes/form";
		}
		Note saved = noteService.create(form);
		return "redirect:/notes/" + saved.getId();
	}

	@GetMapping("/{id}")
	public String detail(@PathVariable Long id, Model model) {
		model.addAttribute("pageTitle", "메모 상세");
		model.addAttribute("note", noteService.findById(id));
		return "notes/detail";
	}

	@GetMapping("/{id}/edit")
	public String editForm(@PathVariable Long id, Model model) {
		Note note = noteService.findById(id);
		NoteForm form = new NoteForm();
		form.setTitle(note.getTitle());
		form.setContent(note.getContent());
		model.addAttribute("pageTitle", "메모 수정");
		model.addAttribute("noteId", id);
		model.addAttribute("noteForm", form);
		return "notes/edit";
	}

	@PostMapping("/{id}")
	public String update(
			@PathVariable Long id,
			@Valid @ModelAttribute("noteForm") NoteForm form,
			BindingResult bindingResult,
			Model model) {
		if (bindingResult.hasErrors()) {
			model.addAttribute("pageTitle", "메모 수정");
			model.addAttribute("noteId", id);
			return "notes/edit";
		}
		noteService.update(id, form);
		return "redirect:/notes/" + id;
	}

	@PostMapping("/{id}/delete")
	public String delete(@PathVariable Long id) {
		noteService.delete(id);
		return "redirect:/notes";
	}
}
