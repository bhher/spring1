package com.example.hospital.api.error;

import com.example.hospital.reservation.exception.NotFoundException;
import com.example.hospital.reservation.exception.SlotAlreadyReservedException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiError> notFound(NotFoundException e) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiError.of("NOT_FOUND", e.getMessage()));
	}

	@ExceptionHandler({SlotAlreadyReservedException.class, OptimisticLockingFailureException.class})
	public ResponseEntity<ApiError> conflict(Exception e) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiError.of("SLOT_ALREADY_RESERVED", e.getMessage()));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiError> badRequest(IllegalArgumentException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiError.of("BAD_REQUEST", e.getMessage()));
	}
}