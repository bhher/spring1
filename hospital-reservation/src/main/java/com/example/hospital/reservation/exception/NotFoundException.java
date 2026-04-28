package com.example.hospital.reservation.exception;

public class NotFoundException extends RuntimeException {
	public NotFoundException(String message) {
		super(message);
	}
}