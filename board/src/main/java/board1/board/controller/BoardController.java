package board1.board.controller;

import board1.board.entity.Board;
import board1.board.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
public class BoardController {

    @Autowired
    private BoardService boardService;

    @GetMapping("/form")
    public String boardForm(Model model){
        model.addAttribute("board", new Board());
        return "form";
        //form.html과 연결됨
    }

    @GetMapping
    public String boardList(Model model){
        model.addAttribute("boardList",boardService.findAll());
        return "list";
    }

    @GetMapping("/{id}")
    public String boardDetail(@PathVariable Long id, Model model){
        Board board = boardService.findById(id).orElseThrow();
        model.addAttribute("board",board);
        return "detail";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model){
        Board existing = boardService.findById(id).orElseThrow();
        model.addAttribute("board",existing);
        return "edit";
    }

    @GetMapping("/delete/{id}")
    public String deleteBoard(@PathVariable Long id){
        boardService.deleteById(id);
        return "list";
    }

    @PostMapping("/save")
    public String boardSave(Board board){
        boardService.save(board);
        return "redirect:/board";
    }

    @PostMapping("/edit/{id}")
    public String editBoard(@PathVariable Long id, @ModelAttribute Board board) {
        Board existing = boardService.findById(id).orElseThrow();
        existing.setTitle(board.getTitle());
        existing.setContent(board.getContent());
        boardService.save(existing);
        return "redirect:/board/" + id;
    }

}
