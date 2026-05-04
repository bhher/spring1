package com.example.thymeleafexamples.config;

import com.example.thymeleafexamples.domain.DemoItem;
import com.example.thymeleafexamples.repository.DemoItemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleDemoItemDataLoader {

	@Bean
	CommandLineRunner loadSampleDemoItems(DemoItemRepository demoItemRepository) {
		return args -> {
			if (demoItemRepository.count() > 0) {
				return;
			}
			demoItemRepository.save(new DemoItem("사과", 10));
			demoItemRepository.save(new DemoItem("바나나", 5));
			demoItemRepository.save(new DemoItem("오렌지", 2));
		};
	}
}
