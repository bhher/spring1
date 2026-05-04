package com.example.thymeleafexamples.repository;

import com.example.thymeleafexamples.domain.DemoItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoItemRepository extends JpaRepository<DemoItem, Long> {

	List<DemoItem> findAllByOrderByIdAsc();
}
