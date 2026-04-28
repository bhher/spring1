package com.example.hospital.doctor.api.response;

import com.example.hospital.doctor.entity.Doctor;

public record DoctorResponse(
		Long id,
		String name,
		String specialty
) {
	public static DoctorResponse from(Doctor d) {
		return new DoctorResponse(d.getId(), d.getName(), d.getSpecialty());
	}
}