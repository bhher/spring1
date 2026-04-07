package com.example.mcp1.demo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

/**
 * Tool Calling 데모: 모델이 주문 조회가 필요할 때 Java 함수를 호출합니다.
 */
@Configuration
public class OrderTools {

	private final Map<String, OrderStatusResponse> mockDb = new ConcurrentHashMap<>(Map.of(
			"ORD-1001", new OrderStatusResponse("ORD-1001", "SHIPPED", "서울 강남"),
			"ORD-2002", new OrderStatusResponse("ORD-2002", "PROCESSING", "부산 해운대")));

	@Bean
	public java.util.function.Function<OrderStatusRequest, OrderStatusResponse> orderStatusTool() {
		return request -> mockDb.getOrDefault(
				request.orderId(),
				new OrderStatusResponse(request.orderId(), "NOT_FOUND", "-"));
	}

	public record OrderStatusRequest(
			@JsonPropertyDescription("조회할 주문 ID (예: ORD-1001)") String orderId) {
	}

	public record OrderStatusResponse(String orderId, String status, String warehouse) {
	}

}
