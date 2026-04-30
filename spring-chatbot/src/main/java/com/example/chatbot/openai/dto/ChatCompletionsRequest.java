package com.example.chatbot.openai.dto;

import java.util.List;

public record ChatCompletionsRequest(
		String model,
		List<Message> messages
) {
	public record Message(String role, String content) {
	}
}

