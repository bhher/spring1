package com.example.mcp1.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiChatConfig {

	@Bean
	public ChatMemory chatMemory() {
		return MessageWindowChatMemory.builder()
				.maxMessages(30)
				.build();
	}

	@Bean
	public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
		return builder
				.defaultSystem("You are a backend-focused assistant. Be concise and technical.")
				.defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
				.defaultToolNames("orderStatusTool")
				.build();
	}

}
