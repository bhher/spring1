package com.example.hospital.reservation.api.response;

import com.example.hospital.reservation.entity.Reservation;
import com.example.hospital.reservation.entity.ReservationStatus;

import java.time.Instant;

public record ReservationResponse(
		Long id,
		Long slotId,
		String patientName,
		String patientPhone,
		ReservationStatus status,
		Instant createdAt,
		Instant cancelledAt
) {
	public static ReservationResponse from(Reservation r) {
		return new ReservationResponse(
				r.getId(),
				r.getSlot().getId(),
				r.getPatientName(),
				r.getPatientPhone(),
				r.getStatus(),
				r.getCreatedAt(),
				r.getCancelledAt()
		);
	}
}