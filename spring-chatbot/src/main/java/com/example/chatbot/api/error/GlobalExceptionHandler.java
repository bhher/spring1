package com.example.chatbot.api.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> validation(MethodArgumentNotValidException e) {
		return ResponseEntity.badRequest().body(ApiError.of("BAD_REQUEST", "요청 값이 올바르지 않습니다."));
	}

	@ExceptionHandler(IllegalStateException.class)
	public ResponseEntity<ApiError> illegalState(IllegalStateException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("CONFIG_ERROR", e.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException e) {
		return ResponseEntity.badRequest().body(ApiError.of("BAD_REQUEST", "JSON 형식이 올바르지 않습니다."));
	}

	@ExceptionHandler(HttpClientErrorException.class)
	public ResponseEntity<ApiError> http4xx(HttpClientErrorException e) {
		HttpStatus status = HttpStatus.resolve(e.getStatusCode().value());
		if (status == null) status = HttpStatus.BAD_REQUEST;
		return ResponseEntity.status(status).body(ApiError.of("OPENAI_4XX", e.getResponseBodyAsString()));
	}

	@ExceptionHandler(HttpServerErrorException.class)
	public ResponseEntity<ApiError> http5xx(HttpServerErrorException e) {
		return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(ApiError.of("OPENAI_5XX", e.getResponseBodyAsString()));
	}
}

