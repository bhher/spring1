package com.example.hospital.doctor.repository;

import com.example.hospital.doctor.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}