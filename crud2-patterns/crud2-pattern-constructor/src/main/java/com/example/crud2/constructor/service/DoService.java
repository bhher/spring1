package com.example.crud2.constructor.service;

import com.example.crud2.constructor.dto.DoDto;
import com.example.crud2.constructor.entity.DoIt;
import com.example.crud2.constructor.repository.DoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 생성자: {@code new DoIt(null, title, content)} / 수정 시 {@code new DoIt(num, title, content)} 저장. */
@Service
@Transactional(readOnly = true)
public class DoService {

	private final DoRepository doRepository;

	public DoService(DoRepository doRepository) {
		this.doRepository = doRepository;
	}

	public List<DoIt> findAll() {
		return doRepository.findAll();
	}

	public Optional<DoIt> findById(Long num) {
		return doRepository.findById(num);
	}

	@Transactional
	public DoIt create(DoDto dto) {
		DoIt entity = new DoIt(null, dto.getTitle(), dto.getContent());
		return doRepository.save(entity);
	}

	@Transactional
	public Optional<DoIt> update(DoDto dto) {
		if (dto.getNum() == null) {
			return Optional.empty();
		}
		return doRepository.findById(dto.getNum()).map(existing -> {
			DoIt merged = new DoIt(dto.getNum(), dto.getTitle(), dto.getContent());
			return doRepository.save(merged);
		});
	}

	@Transactional
	public boolean delete(Long num) {
		return doRepository.findById(num)
				.map(entity -> {
					doRepository.delete(entity);
					return true;
				})
				.orElse(false);
	}
}
