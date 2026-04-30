package com.example.chatbot.api;

import com.example.chatbot.api.request.ChatRequestDto;
import com.example.chatbot.api.response.ChatResponseDto;
import com.example.chatbot.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

	private final ChatService chatService;

	public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@PostMapping("/chat")
	public ChatResponseDto chat(@Valid @RequestBody ChatRequestDto req) {
		return ChatResponseDto.of(chatService.ask(req.message()));
	}

	/**
	 * 간단 테스트용: JSON 문자열(body가 "안녕") 또는 text/plain("안녕") 형태로 호출 가능
	 */
	@PostMapping("/chat/text")
	public ChatResponseDto chatText(@RequestBody String userMessage) {
		return ChatResponseDto.of(chatService.ask(userMessage));
	}
}

