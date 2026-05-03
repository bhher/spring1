package com.example.memberboard.domain.board.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardWriteDto {

	@NotBlank
	@Size(max = 200)
	private String title;

	@NotBlank
	private String content;
}
