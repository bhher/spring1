package com.example.crud1.repository;

import com.example.crud1.entity.DoIt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DoRepository extends JpaRepository<DoIt,Long>{
    @Override
    List<DoIt> findAll();
}
