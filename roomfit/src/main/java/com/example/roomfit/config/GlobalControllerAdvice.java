package com.example.roomfit.config;

import com.example.roomfit.exception.BusinessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalControllerAdvice {

	@ExceptionHandler(BusinessException.class)
	public String handleBusiness(BusinessException ex, RedirectAttributes ra) {
		ra.addFlashAttribute("error", ex.getMessage());
		return "redirect:/";
	}
}
