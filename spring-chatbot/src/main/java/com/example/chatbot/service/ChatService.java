package com.example.chatbot.service;

import com.example.chatbot.config.OpenAiProperties;
import com.example.chatbot.openai.OpenAiClient;
import com.example.chatbot.openai.dto.ChatCompletionsRequest;
import com.example.chatbot.openai.dto.ChatCompletionsResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

	private final OpenAiClient openAiClient;
	private final OpenAiProperties props;

	public ChatService(OpenAiClient openAiClient, OpenAiProperties props) {
		this.openAiClient = openAiClient;
		this.props = props;
	}

	public String ask(String userMessage) {
		if (props.apiKey() == null || props.apiKey().isBlank()) {
			throw new IllegalStateException("OPENAI_API_KEY가 설정되어 있지 않습니다. 환경변수를 설정하세요.");
		}

		ChatCompletionsRequest req = new ChatCompletionsRequest(
				props.model(),
				List.of(
						new ChatCompletionsRequest.Message("system", "You are a helpful assistant. Answer in Korean."),
						new ChatCompletionsRequest.Message("user", userMessage)
				)
		);

		ChatCompletionsResponse res = openAiClient.chatCompletions(req);
		if (res == null || res.choices() == null || res.choices().isEmpty() || res.choices().get(0).message() == null) {
			throw new IllegalStateException("OpenAI 응답이 비어 있습니다.");
		}

		return res.choices().get(0).message().content();
	}
}

