package com.example.hospital.slot.api.response;

import com.example.hospital.slot.entity.AppointmentSlot;

import java.time.LocalDateTime;

public record SlotResponse(
		Long id,
		Long doctorId,
		LocalDateTime startAt,
		LocalDateTime endAt,
		boolean reserved
) {
	public static SlotResponse from(AppointmentSlot s) {
		return new SlotResponse(
				s.getId(),
				s.getDoctor().getId(),
				s.getStartAt(),
				s.getEndAt(),
				s.isReserved()
		);
	}
}