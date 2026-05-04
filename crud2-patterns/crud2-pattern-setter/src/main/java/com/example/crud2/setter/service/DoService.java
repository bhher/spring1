package com.example.crud2.setter.service;

import com.example.crud2.setter.dto.DoDto;
import com.example.crud2.setter.entity.DoIt;
import com.example.crud2.setter.repository.DoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Setter: {@code new DoIt()} 후 setTitle / setContent, 수정도 setter. */
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
		DoIt entity = new DoIt();
		entity.setTitle(dto.getTitle());
		entity.setContent(dto.getContent());
		return doRepository.save(entity);
	}

	@Transactional
	public Optional<DoIt> update(DoDto dto) {
		if (dto.getNum() == null) {
			return Optional.empty();
		}
		return doRepository.findById(dto.getNum()).map(existing -> {
			existing.setTitle(dto.getTitle());
			existing.setContent(dto.getContent());
			return doRepository.save(existing);
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
