package com.example.hospital.slot.service;

import com.example.hospital.doctor.entity.Doctor;
import com.example.hospital.doctor.service.DoctorService;
import com.example.hospital.slot.entity.AppointmentSlot;
import com.example.hospital.slot.repository.AppointmentSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SlotService {

	private final AppointmentSlotRepository slotRepository;
	private final DoctorService doctorService;

	public SlotService(AppointmentSlotRepository slotRepository, DoctorService doctorService) {
		this.slotRepository = slotRepository;
		this.doctorService = doctorService;
	}

	public List<AppointmentSlot> listSlots(Long doctorId, LocalDate date) {
		LocalDateTime from = date.atStartOfDay();
		LocalDateTime to = date.atTime(LocalTime.MAX);
		return slotRepository.findByDoctorIdAndStartAtBetweenOrderByStartAtAsc(doctorId, from, to);
	}

	@Transactional
	public void generateSlots(Long doctorId, LocalDate date, int minutesPerSlot, int startHour, int endHourExclusive) {
		Doctor doctor = doctorService.get(doctorId);
		LocalDateTime start = date.atTime(startHour, 0);
		LocalDateTime end = date.atTime(endHourExclusive, 0);

		LocalDateTime cursor = start;
		while (cursor.isBefore(end)) {
			LocalDateTime next = cursor.plusMinutes(minutesPerSlot);
			AppointmentSlot slot = new AppointmentSlot(null, doctor, cursor, next, false, null);
			slotRepository.save(slot);
			cursor = next;
		}
	}

	public AppointmentSlot get(Long slotId) {
		return slotRepository.findById(slotId)
				.orElseThrow(() -> new com.example.hospital.reservation.exception.NotFoundException("슬롯을 찾을 수 없습니다. id=" + slotId));
	}
}