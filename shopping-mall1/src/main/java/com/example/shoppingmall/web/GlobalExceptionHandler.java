package com.example.shoppingmall.web;

import com.example.shoppingmall.exception.BusinessException;
import com.example.shoppingmall.exception.FileUploadException;
import com.example.shoppingmall.exception.ForbiddenAccessException;
import com.example.shoppingmall.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

/**
 * Thymeleaf 화면용 전역 예외 처리.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ModelAndView notFound(ResourceNotFoundException ex) {
		ModelAndView mv = new ModelAndView("error/404");
		mv.addObject("message", ex.getMessage());
		return mv;
	}

	@ExceptionHandler(ForbiddenAccessException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView forbidden(ForbiddenAccessException ex) {
		ModelAndView mv = new ModelAndView("error/403");
		mv.addObject("message", ex.getMessage());
		return mv;
	}

	@ExceptionHandler(AccessDeniedException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ModelAndView accessDenied(AccessDeniedException ex) {
		ModelAndView mv = new ModelAndView("error/403");
		mv.addObject("message", "이 페이지에 접근할 권한이 없습니다.");
		return mv;
	}

	@ExceptionHandler({BusinessException.class, IllegalStateException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView badRequest(RuntimeException ex) {
		ModelAndView mv = new ModelAndView("error/general");
		mv.addObject("message", ex.getMessage());
		return mv;
	}

	@ExceptionHandler(FileUploadException.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView fileError(FileUploadException ex) {
		ModelAndView mv = new ModelAndView("error/general");
		mv.addObject("message", ex.getMessage());
		return mv;
	}
}
