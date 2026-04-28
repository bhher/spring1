package com.example.hospital.doctor.api.request;

import jakarta.validation.constraints.NotBlank;

public record CreateDoctorRequest(
		@NotBlank String name,
		@NotBlank String specialty
) {
}