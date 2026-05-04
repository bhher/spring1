package com.example.thymeleafexamples.service;

import com.example.thymeleafexamples.domain.DemoItem;
import com.example.thymeleafexamples.repository.DemoItemRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoItemService {

	private final DemoItemRepository demoItemRepository;

	public DemoItemService(DemoItemRepository demoItemRepository) {
		this.demoItemRepository = demoItemRepository;
	}

	@Transactional(readOnly = true)
	public List<DemoItem> findAllOrdered() {
		return demoItemRepository.findAllByOrderByIdAsc();
	}
}
