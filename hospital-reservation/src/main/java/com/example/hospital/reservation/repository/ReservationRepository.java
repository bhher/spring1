package com.example.hospital.reservation.repository;

import com.example.hospital.reservation.entity.Reservation;
import com.example.hospital.reservation.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findByStatusAndReminderSentAtIsNullAndCreatedAtBetween(ReservationStatus status, Instant from, Instant to);
}