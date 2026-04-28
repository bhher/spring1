package com.example.hospital.seed;

import com.example.hospital.doctor.entity.Doctor;
import com.example.hospital.doctor.repository.DoctorRepository;
import com.example.hospital.slot.service.SlotService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SeedDataRunner implements CommandLineRunner {

	private final DoctorRepository doctorRepository;
	private final SlotService slotService;

	public SeedDataRunner(DoctorRepository doctorRepository, SlotService slotService) {
		this.doctorRepository = doctorRepository;
		this.slotService = slotService;
	}

	@Override
	public void run(String... args) {
		if (doctorRepository.count() > 0) {
			return;
		}

		Doctor kim = doctorRepository.save(new Doctor(null, "김의사", "내과"));
		Doctor park = doctorRepository.save(new Doctor(null, "박의사", "정형외과"));

		LocalDate today = LocalDate.now();
		slotService.generateSlots(kim.getId(), today, 30, 9, 12);
		slotService.generateSlots(park.getId(), today, 30, 13, 17);
	}
}