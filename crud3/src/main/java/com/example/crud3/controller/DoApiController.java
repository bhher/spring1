package com.example.crud3.controller;

import com.example.crud3.dto.DoDto;
import com.example.crud3.entity.DoIt;
import com.example.crud3.service.DoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/doits")
public class DoApiController {

	private final DoService doService;

	public DoApiController(DoService doService) {
		this.doService = doService;
	}

	@GetMapping
	public List<DoIt> list() {
		return doService.findAll();
	}

	@GetMapping("/{num}")
	public ResponseEntity<DoIt> get(@PathVariable("num") Long num) {
		return doService.findById(num)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@PostMapping
	public ResponseEntity<DoIt> create(@RequestBody DoDto dto) {
		DoIt saved = doService.create(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	@PutMapping("/{num}")
	public ResponseEntity<DoIt> update(@PathVariable("num") Long num, @RequestBody DoDto dto) {
		dto.setNum(num);
		return doService.update(dto)
				.map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{num}")
	public ResponseEntity<Void> delete(@PathVariable("num") Long num) {
		if (doService.delete(num)) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.notFound().build();
	}
}
