package com.example.hospital.reservation.api.request;

import jakarta.validation.constraints.NotNull;

public record CancelReservationRequest(
		@NotNull Long reservationId
) {
}