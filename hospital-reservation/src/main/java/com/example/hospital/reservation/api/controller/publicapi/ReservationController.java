package com.example.hospital.reservation.api.controller.publicapi;

import com.example.hospital.reservation.api.request.CancelReservationRequest;
import com.example.hospital.reservation.api.request.CreateReservationRequest;
import com.example.hospital.reservation.api.response.ReservationResponse;
import com.example.hospital.reservation.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

	private final ReservationService reservationService;

	public ReservationController(ReservationService reservationService) {
		this.reservationService = reservationService;
	}

	@GetMapping
	public List<ReservationResponse> list() {
		return reservationService.findAll().stream().map(ReservationResponse::from).toList();
	}

	@GetMapping("/{id}")
	public ReservationResponse get(@PathVariable("id") Long id) {
		return ReservationResponse.from(reservationService.get(id));
	}

	@PostMapping
	public ResponseEntity<ReservationResponse> create(@Valid @RequestBody CreateReservationRequest req) {
		var saved = reservationService.create(req.slotId(), req.patientName(), req.patientPhone());
		return ResponseEntity.status(HttpStatus.CREATED).body(ReservationResponse.from(saved));
	}

	@PostMapping("/cancel")
	public ReservationResponse cancel(@Valid @RequestBody CancelReservationRequest req) {
		return ReservationResponse.from(reservationService.cancel(req.reservationId()));
	}
}