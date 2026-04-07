package com.example.mcp1.demo;

import java.net.URI;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.content.Media;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;

@Service
public class AiDemoService {

	private final ChatClient chatClient;

	public AiDemoService(ChatClient chatClient) {
		this.chatClient = chatClient;
	}

	public String chatWithMemory(String conversationId, String userMessage) {
		return chatClient.prompt()
				.advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
				.user(userMessage)
				.call()
				.content();
	}

	public String askWithTools(String userMessage) {
		return chatClient.prompt()
				.user(userMessage)
				.call()
				.content();
	}

	public ProductSummary structuredProduct(String topicHint) {
		return chatClient.prompt()
				.user("다음 주제에 맞는 가상의 이커머스 상품 하나를 JSON에 맞게 생성하라: " + topicHint)
				.call()
				.entity(ProductSummary.class);
	}

	/**
	 * BeanOutputConverter 를 명시적으로 사용하는 Structured Output 예시 (스트리밍 집계 전 변환 등에도 활용).
	 */
	public ProductSummary structuredWithExplicitConverter(String topicHint) {
		var converter = new BeanOutputConverter<>(new ParameterizedTypeReference<ProductSummary>() {
		});
		String raw = chatClient.prompt()
				.user(u -> u.text(
						"가상의 상품 하나를 설명하라. 반드시 아래 형식을 따르라.\n{format}\n주제: " + topicHint)
						.param("format", converter.getFormat()))
				.call()
				.content();
		return converter.convert(raw);
	}

	public String describeImage(String imageUrl, String prompt) {
		try {
			var uri = URI.create(imageUrl);
			Media media = new Media(MimeType.valueOf("image/jpeg"), uri);
			return chatClient.prompt()
					.user(u -> u.text(prompt).media(media))
					.call()
					.content();
		}
		catch (Exception e) {
			throw new IllegalArgumentException("유효한 이미지 URL 이 필요합니다.", e);
		}
	}

}
