package com.example.chatbot.openai.dto;

import java.util.List;

public record ChatCompletionsResponse(
		List<Choice> choices
) {
	public record Choice(Message message) {
	}

	public record Message(String role, String content) {
	}
}

