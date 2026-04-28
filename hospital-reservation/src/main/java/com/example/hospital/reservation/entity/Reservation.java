package com.example.hospital.reservation.entity;

import com.example.hospital.slot.entity.AppointmentSlot;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "slot_id", nullable = false)
	private AppointmentSlot slot;

	private String patientName;
	private String patientPhone;

	@Enumerated(EnumType.STRING)
	private ReservationStatus status;

	private Instant createdAt;
	private Instant cancelledAt;

	// 데모용: 리마인더 스케줄러가 1회 발송(로그) 후 기록
	private Instant reminderSentAt;

	public static Reservation booked(AppointmentSlot slot, String patientName, String patientPhone) {
		return new Reservation(
				null,
				slot,
				patientName,
				patientPhone,
				ReservationStatus.BOOKED,
				Instant.now(),
				null,
				null
		);
	}

	public void cancel() {
		if (this.status == ReservationStatus.CANCELLED) {
			return;
		}
		this.status = ReservationStatus.CANCELLED;
		this.cancelledAt = Instant.now();
	}

	public void markReminderSent() {
		this.reminderSentAt = Instant.now();
	}
}