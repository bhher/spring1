package com.example.crud2.mapper.service;

import com.example.crud2.mapper.dto.DoDto;
import com.example.crud2.mapper.entity.DoIt;
import com.example.crud2.mapper.repository.DoRepository;
import com.example.crud2.mapper.support.DoMapper;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		return doRepository.save(DoMapper.toNewEntity(dto));
	}

	@Transactional
	public Optional<DoIt> update(DoDto dto) {
		if (dto.getNum() == null) {
			return Optional.empty();
		}
		return doRepository.findById(dto.getNum()).map(existing ->
				doRepository.save(DoMapper.toEntityWithId(dto)));
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
