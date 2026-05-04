package com.example.boardlogin.setter.web;

import com.example.boardlogin.setter.domain.Board;
import com.example.boardlogin.setter.domain.User;
import com.example.boardlogin.setter.service.BoardService;
import com.example.boardlogin.setter.service.UserService;
import com.example.boardlogin.setter.web.dto.BoardForm;
import jakarta.servlet.http.HttpSession;
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
@RequestMapping("/posts")
public class BoardController {

    private final BoardService boardService;
    private final UserService userService;

    public BoardController(BoardService boardService, UserService userService) {
        this.boardService = boardService;
        this.userService = userService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("posts", boardService.findAll());
        return "board/list";
    }

    @GetMapping("/{id:\\d+}")
    public String detail(@PathVariable Long id, HttpSession session, Model model) {
        Board board = boardService.findById(id);
        model.addAttribute("post", board);
        model.addAttribute("canEdit", canEdit(session, board));
        return "board/detail";
    }

    @GetMapping("/write")
    public String writeForm(Model model) {
        model.addAttribute("form", new BoardForm());
        return "board/write";
    }

    @PostMapping("/write")
    public String write(
            @Valid @ModelAttribute("form") BoardForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "board/write";
        }
        String username = (String) session.getAttribute(AuthController.SESSION_USER);
        User author = userService.findByUsername(username);
        if (author == null) {
            return "redirect:/login";
        }
        boardService.create(form, author);
        return "redirect:/posts";
    }

    @GetMapping("/{id:\\d+}/edit")
    public String editForm(@PathVariable Long id, HttpSession session, Model model) {
        Board board = boardService.findById(id);
        if (!canEdit(session, board)) {
            return "redirect:/posts/" + id;
        }
        BoardForm form = new BoardForm();
        form.setTitle(board.getTitle());
        form.setContent(board.getContent());
        model.addAttribute("form", form);
        model.addAttribute("postId", id);
        return "board/edit";
    }

    @PostMapping("/{id:\\d+}/edit")
    public String edit(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") BoardForm form,
            BindingResult bindingResult,
            HttpSession session,
            Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("postId", id);
            return "board/edit";
        }
        String username = (String) session.getAttribute(AuthController.SESSION_USER);
        try {
            boardService.update(id, form, username);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("postId", id);
            return "board/edit";
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id:\\d+}/delete")
    public String delete(@PathVariable Long id, HttpSession session, Model model) {
        String username = (String) session.getAttribute(AuthController.SESSION_USER);
        try {
            boardService.delete(id, username);
        } catch (IllegalStateException e) {
            model.addAttribute("error", e.getMessage());
            Board board = boardService.findById(id);
            model.addAttribute("post", board);
            model.addAttribute("canEdit", canEdit(session, board));
            return "board/detail";
        }
        return "redirect:/posts";
    }

    private static boolean canEdit(HttpSession session, Board board) {
        Object u = session.getAttribute(AuthController.SESSION_USER);
        if (u == null) {
            return false;
        }
        return u.toString().equals(board.getAuthor().getUsername());
    }
}
