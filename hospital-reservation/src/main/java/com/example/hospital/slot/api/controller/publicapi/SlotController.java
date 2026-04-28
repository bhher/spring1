package com.example.hospital.slot.api.controller.publicapi;

import com.example.hospital.slot.api.response.SlotResponse;
import com.example.hospital.slot.service.SlotService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/slots")
public class SlotController {

	private final SlotService slotService;

	public SlotController(SlotService slotService) {
		this.slotService = slotService;
	}

	@GetMapping
	public List<SlotResponse> list(
			@PathVariable("doctorId") Long doctorId,
			@RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
	) {
		return slotService.listSlots(doctorId, date).stream().map(SlotResponse::from).toList();
	}
}