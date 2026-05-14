package com.example.shoppingmall.web.api;

import com.example.shoppingmall.exception.BusinessException;
import com.example.shoppingmall.exception.FileUploadException;
import com.example.shoppingmall.exception.ForbiddenAccessException;
import com.example.shoppingmall.exception.ResourceNotFoundException;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<Map<String, String>> notFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler({ForbiddenAccessException.class, AccessDeniedException.class})
	public ResponseEntity<Map<String, String>> forbidden(RuntimeException ex) {
		String msg = ex instanceof AccessDeniedException ? "이 리소스에 접근할 권한이 없습니다." : ex.getMessage();
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", msg));
	}

	@ExceptionHandler({BusinessException.class, IllegalStateException.class})
	public ResponseEntity<Map<String, String>> badRequest(RuntimeException ex) {
		return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
	}

	@ExceptionHandler(FileUploadException.class)
	public ResponseEntity<Map<String, String>> fileError(FileUploadException ex) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", ex.getMessage()));
	}
}
