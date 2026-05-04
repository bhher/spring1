package com.example.thymeleafexamples.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NoteForm {

	@NotBlank(message = "제목을 입력하세요.")
	@Size(max = 200, message = "제목은 200자 이하입니다.")
	private String title = "";

	@NotBlank(message = "내용을 입력하세요.")
	@Size(max = 8000, message = "내용은 8000자 이하입니다.")
	private String content = "";

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
