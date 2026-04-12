package board01.board.controller;

import board01.board.dto.BoardEditDto;
import board01.board.dto.BoardEditFormDto;
import board01.board.dto.BoardResponseDto;
import board01.board.dto.BoardWriteDto;
import board01.board.service.BoardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/board")
public class BoardController {

	private final BoardService boardService;

	public BoardController(BoardService boardService) {
		this.boardService = boardService;
	}

	@GetMapping("/form")
	public String boardForm(Model model) {
		model.addAttribute("board", new BoardWriteDto());
		return "form";
	}

	@GetMapping
	public String boardList(Model model) {
		model.addAttribute("boardList", boardService.findAllDtos());
		return "list";
	}

	@GetMapping("/{id}")
	public String boardDetail(@PathVariable Long id, Model model) {
		BoardResponseDto board = boardService.findResponseById(id).orElseThrow();
		model.addAttribute("board", board);
		return "detail";
	}

	@GetMapping("/edit/{id}")
	public String editForm(@PathVariable Long id, Model model) {
		BoardEditFormDto board = boardService.findEditFormById(id).orElseThrow();
		model.addAttribute("board", board);
		return "edit";
	}

	@GetMapping("/delete/{id}")
	public String deleteBoard(@PathVariable Long id) {
		boardService.deleteById(id);
		return "redirect:/board";
	}

	@PostMapping("/save")
	public String boardSave(@ModelAttribute BoardWriteDto board) {
		boardService.saveFromWrite(board);
		return "redirect:/board";
	}

	@PostMapping("/edit/{id}")
	public String editBoard(@PathVariable Long id, @ModelAttribute BoardEditDto board) {
		boardService.update(id, board);
		return "redirect:/board/" + id;
	}
}
