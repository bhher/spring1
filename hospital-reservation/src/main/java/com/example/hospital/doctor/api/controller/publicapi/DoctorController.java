package com.example.hospital.doctor.api.controller.publicapi;

import com.example.hospital.doctor.api.response.DoctorResponse;
import com.example.hospital.doctor.service.DoctorService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

	private final DoctorService doctorService;

	public DoctorController(DoctorService doctorService) {
		this.doctorService = doctorService;
	}

	@GetMapping
	public List<DoctorResponse> list() {
		return doctorService.findAll().stream().map(DoctorResponse::from).toList();
	}

	@GetMapping("/{id}")
	public DoctorResponse get(@PathVariable("id") Long id) {
		return DoctorResponse.from(doctorService.get(id));
	}
}