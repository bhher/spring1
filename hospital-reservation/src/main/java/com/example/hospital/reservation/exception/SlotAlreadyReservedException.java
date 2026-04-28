package com.example.hospital.reservation.exception;

public class SlotAlreadyReservedException extends RuntimeException {
	public SlotAlreadyReservedException(String message) {
		super(message);
	}
}