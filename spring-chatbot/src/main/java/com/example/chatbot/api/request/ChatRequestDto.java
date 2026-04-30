package com.example.chatbot.api.request;

import jakarta.validation.constraints.NotBlank;

public record ChatRequestDto(
		@NotBlank String message
) {
}

