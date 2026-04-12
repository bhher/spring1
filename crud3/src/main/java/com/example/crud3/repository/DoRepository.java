package com.example.crud3.repository;

import com.example.crud3.entity.DoIt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoRepository extends JpaRepository<DoIt, Long> {

	@Override
	List<DoIt> findAll();
}
