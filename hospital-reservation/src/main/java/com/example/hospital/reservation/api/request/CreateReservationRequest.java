package com.example.hospital.reservation.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateReservationRequest(
		@NotNull Long slotId,
		@NotBlank String patientName,
		String patientPhone
) {
}