package com.example.hospital.slot.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record GenerateSlotsRequest(
		@NotNull LocalDate date,
		@Min(5) @Max(120) int minutesPerSlot,
		@Min(0) @Max(23) int startHour,
		@Min(1) @Max(24) int endHourExclusive
) {
}