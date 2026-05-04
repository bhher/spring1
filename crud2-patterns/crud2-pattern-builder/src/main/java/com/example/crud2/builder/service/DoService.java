package com.example.crud2.builder.service;

import com.example.crud2.builder.dto.DoDto;
import com.example.crud2.builder.entity.DoIt;
import com.example.crud2.builder.repository.DoRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Builder: Lombok {@code DoIt.builder()...build()} 로 엔티티 조립. */
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
		DoIt doit = DoIt.builder()
				.title(dto.getTitle())
				.content(dto.getContent())
				.build();
		return doRepository.save(doit);
	}

	@Transactional
	public Optional<DoIt> update(DoDto dto) {
		if (dto.getNum() == null) {
			return Optional.empty();
		}
		return doRepository.findById(dto.getNum()).map(existing -> {
			DoIt doit = DoIt.builder()
					.num(dto.getNum())
					.title(dto.getTitle())
					.content(dto.getContent())
					.build();
			return doRepository.save(doit);
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
