package com.example.crud2.setter.repository;

import com.example.crud2.setter.entity.DoIt;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoRepository extends JpaRepository<DoIt, Long> {

	@Override
	List<DoIt> findAll();
}
