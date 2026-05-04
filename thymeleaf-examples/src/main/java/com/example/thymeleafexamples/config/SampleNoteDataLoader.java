package com.example.thymeleafexamples.config;

import com.example.thymeleafexamples.domain.Note;
import com.example.thymeleafexamples.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SampleNoteDataLoader {

	@Bean
	CommandLineRunner loadSampleNotes(NoteRepository noteRepository) {
		return args -> {
			if (noteRepository.count() > 0) {
				return;
			}
			noteRepository.save(Note.create(
					"첫 메모",
					"H2 파일 DB에 저장됩니다. 목록은 Repository → Service → Controller 순으로 조회합니다."));
			noteRepository.save(Note.create(
					"Thymeleaf",
					"templates/notes/*.html 에서 th:each, th:text 등을 실제 데이터와 함께 써 볼 수 있습니다."));
		};
	}
}
