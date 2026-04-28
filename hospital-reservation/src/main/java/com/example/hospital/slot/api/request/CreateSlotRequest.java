package com.example.hospital.slot.api.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateSlotRequest(
		@NotNull Long doctorId,
		@NotNull LocalDateTime startAt,
		@NotNull LocalDateTime endAt
) {
}