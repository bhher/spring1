package com.example.crud2.constructor.repository;

import com.example.crud2.constructor.entity.DoIt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoRepository extends JpaRepository<DoIt, Long> {

	@Override
	List<DoIt> findAll();
}
