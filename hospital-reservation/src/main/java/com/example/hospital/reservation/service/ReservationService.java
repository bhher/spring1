package com.example.hospital.reservation.service;

import com.example.hospital.reservation.entity.Reservation;
import com.example.hospital.reservation.entity.ReservationStatus;
import com.example.hospital.reservation.exception.NotFoundException;
import com.example.hospital.reservation.exception.SlotAlreadyReservedException;
import com.example.hospital.reservation.repository.ReservationRepository;
import com.example.hospital.slot.entity.AppointmentSlot;
import com.example.hospital.slot.repository.AppointmentSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReservationService {

	private final ReservationRepository reservationRepository;
	private final AppointmentSlotRepository slotRepository;
	private final ReservationConcurrencyService concurrencyService;

	public ReservationService(
			ReservationRepository reservationRepository,
			AppointmentSlotRepository slotRepository,
			ReservationConcurrencyService concurrencyService
	) {
		this.reservationRepository = reservationRepository;
		this.slotRepository = slotRepository;
		this.concurrencyService = concurrencyService;
	}

	public List<Reservation> findAll() {
		return reservationRepository.findAll();
	}

	public Reservation get(Long id) {
		return reservationRepository.findById(id)
				.orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다. id=" + id));
	}

	@Transactional
	public Reservation create(Long slotId, String patientName, String patientPhone) {
		if (patientName == null || patientName.isBlank()) {
			throw new IllegalArgumentException("patientName은 필수입니다.");
		}

		return concurrencyService.withOptimisticRetry(3, () -> {
			AppointmentSlot slot = slotRepository.findById(slotId)
					.orElseThrow(() -> new NotFoundException("슬롯을 찾을 수 없습니다. id=" + slotId));

			if (slot.isReserved()) {
				throw new SlotAlreadyReservedException("이미 예약된 시간입니다.");
			}

			slot.markReserved();
			slotRepository.save(slot);

			Reservation reservation = Reservation.booked(slot, patientName, patientPhone);
			return reservationRepository.save(reservation);
		});
	}

	@Transactional
	public Reservation cancel(Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
				.orElseThrow(() -> new NotFoundException("예약을 찾을 수 없습니다. id=" + reservationId));
		reservation.cancel();

		AppointmentSlot slot = reservation.getSlot();
		// CANCELLED로 바꾸면 슬롯도 다시 오픈
		if (reservation.getStatus() == ReservationStatus.CANCELLED) {
			slot.markAvailable();
			slotRepository.save(slot);
		}

		return reservationRepository.save(reservation);
	}
}