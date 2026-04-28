package com.example.hospital.slot.repository;

import com.example.hospital.slot.entity.AppointmentSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentSlotRepository extends JpaRepository<AppointmentSlot, Long> {
	List<AppointmentSlot> findByDoctorIdAndStartAtBetweenOrderByStartAtAsc(Long doctorId, LocalDateTime from, LocalDateTime to);
}