package com.example.thymeleafexamples.repository;

import com.example.thymeleafexamples.domain.Note;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

	List<Note> findAllByOrderByCreatedAtDesc();
}
