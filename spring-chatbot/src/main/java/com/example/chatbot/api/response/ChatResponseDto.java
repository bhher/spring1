package com.example.chatbot.api.response;

public record ChatResponseDto(
		String content
) {
	public static ChatResponseDto of(String content) {
		return new ChatResponseDto(content);
	}
}

