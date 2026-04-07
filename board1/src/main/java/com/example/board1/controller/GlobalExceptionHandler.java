package com.example.board1.controller;

import com.example.board1.exception.PostNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PostNotFoundException.class)
	public String handleNotFound(PostNotFoundException ex, Model model) {
		model.addAttribute("message", ex.getMessage());
		return "error/404";
	}
}
