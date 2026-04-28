package com.example.hospital.reservation.scheduler;

import com.example.hospital.reservation.entity.Reservation;
import com.example.hospital.reservation.entity.ReservationStatus;
import com.example.hospital.reservation.repository.ReservationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Slf4j
@Component
public class ReminderScheduler {

	private final ReservationRepository reservationRepository;

	public ReminderScheduler(ReservationRepository reservationRepository) {
		this.reservationRepository = reservationRepository;
	}

	@Value("${app.reminder.fixed-delay-ms:60000}")
	private long fixedDelayMs;

	@Scheduled(fixedDelayString = "${app.reminder.fixed-delay-ms:60000}")
	@Transactional
	public void sendReminders() {
		// 데모: createdAt 기준으로 5~65분 전 예약을 “다가오는 예약”으로 취급
		Instant now = Instant.now();
		Instant from = now.minus(Duration.ofMinutes(65));
		Instant to = now.minus(Duration.ofMinutes(5));

		List<Reservation> targets = reservationRepository
				.findByStatusAndReminderSentAtIsNullAndCreatedAtBetween(ReservationStatus.BOOKED, from, to);

		for (Reservation r : targets) {
			log.info("[REMINDER] reservationId={}, patient={}, phone={}", r.getId(), r.getPatientName(), r.getPatientPhone());
			r.markReminderSent();
		}
	}
}