package com.example.chatbot.openai;

import com.example.chatbot.config.OpenAiProperties;
import com.example.chatbot.openai.dto.ChatCompletionsRequest;
import com.example.chatbot.openai.dto.ChatCompletionsResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OpenAiClient {

	private final RestClient restClient;
	private final OpenAiProperties props;

	public OpenAiClient(OpenAiProperties props) {
		this.props = props;
		this.restClient = RestClient.builder()
				.baseUrl(props.baseUrl())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
				.build();
	}

	public ChatCompletionsResponse chatCompletions(ChatCompletionsRequest request) {
		return restClient.post()
				.uri(props.chatCompletionsPath())
				.header(HttpHeaders.AUTHORIZATION, "Bearer " + props.apiKey())
				.body(request)
				.retrieve()
				.body(ChatCompletionsResponse.class);
	}
}

