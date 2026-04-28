package com.example.hospital.doctor.service;

import com.example.hospital.doctor.entity.Doctor;
import com.example.hospital.doctor.repository.DoctorRepository;
import com.example.hospital.reservation.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class DoctorService {

	private final DoctorRepository doctorRepository;

	public DoctorService(DoctorRepository doctorRepository) {
		this.doctorRepository = doctorRepository;
	}

	public List<Doctor> findAll() {
		return doctorRepository.findAll();
	}

	public Doctor get(Long id) {
		return doctorRepository.findById(id).orElseThrow(() -> new NotFoundException("의사를 찾을 수 없습니다. id=" + id));
	}
}