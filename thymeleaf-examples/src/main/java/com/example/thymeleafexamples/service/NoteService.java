package com.example.thymeleafexamples.service;

import com.example.thymeleafexamples.domain.Note;
import com.example.thymeleafexamples.repository.NoteRepository;
import com.example.thymeleafexamples.web.dto.NoteForm;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoteService {

	private final NoteRepository noteRepository;

	public NoteService(NoteRepository noteRepository) {
		this.noteRepository = noteRepository;
	}

	@Transactional(readOnly = true)
	public List<Note> findAll() {
		return noteRepository.findAllByOrderByCreatedAtDesc();
	}

	@Transactional(readOnly = true)
	public Note findById(Long id) {
		return noteRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("메모를 찾을 수 없습니다. id=" + id));
	}

	@Transactional
	public Note create(NoteForm form) {
		Note note = Note.create(form.getTitle().trim(), form.getContent().trim());
		return noteRepository.save(note);
	}

	@Transactional
	public void update(Long id, NoteForm form) {
		Note note = findById(id);
		note.update(form.getTitle().trim(), form.getContent().trim());
	}

	@Transactional
	public void delete(Long id) {
		noteRepository.deleteById(id);
	}
}
