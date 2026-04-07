package com.example.mcp1.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiDemoController {

	private final AiDemoService aiDemoService;

	public AiDemoController(AiDemoService aiDemoService) {
		this.aiDemoService = aiDemoService;
	}

	@PostMapping("/memory")
	public ResponseEntity<MemoryResponse> memory(@RequestBody MemoryRequest req) {
		String reply = aiDemoService.chatWithMemory(req.conversationId(), req.message());
		return ResponseEntity.ok(new MemoryResponse(reply));
	}

	@PostMapping("/tools")
	public ResponseEntity<ToolsResponse> tools(@RequestBody ToolsRequest req) {
		String reply = aiDemoService.askWithTools(req.message());
		return ResponseEntity.ok(new ToolsResponse(reply));
	}

	@PostMapping("/structured")
	public ResponseEntity<ProductSummary> structured(@RequestBody StructuredRequest req) {
		return ResponseEntity.ok(aiDemoService.structuredProduct(req.topic()));
	}

	@PostMapping("/structured-converter")
	public ResponseEntity<ProductSummary> structuredConverter(@RequestBody StructuredRequest req) {
		return ResponseEntity.ok(aiDemoService.structuredWithExplicitConverter(req.topic()));
	}

	@PostMapping("/multimodal")
	public ResponseEntity<MultimodalResponse> multimodal(@RequestBody MultimodalRequest req) {
		String reply = aiDemoService.describeImage(req.imageUrl(), req.prompt());
		return ResponseEntity.ok(new MultimodalResponse(reply));
	}

	public record MemoryRequest(String conversationId, String message) {
	}

	public record MemoryResponse(String reply) {
	}

	public record ToolsRequest(String message) {
	}

	public record ToolsResponse(String reply) {
	}

	public record StructuredRequest(String topic) {
	}

	public record MultimodalRequest(String imageUrl, String prompt) {
	}

	public record MultimodalResponse(String reply) {
	}

}
