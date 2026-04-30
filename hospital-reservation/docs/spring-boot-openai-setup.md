# Spring Boot로 ChatGPT(OpenAI) API 연동하기 (1편: 환경 설정)

“스프링으로 AI 챗봇 만들 수 있을까?”  
**가능합니다.** 이 문서는 **Spring Boot에서 OpenAI API를 호출하기 위한 환경 설정**을 단계별로 정리합니다.  
(다음 편에서 실제 챗봇 API를 구성하면 바로 동작합니다.)

---

## 0) 참고: 공식 사이트(실제 링크)

- **OpenAI Platform (대시보드)**: `https://platform.openai.com/`
- **API Key 생성 페이지**: `https://platform.openai.com/settings/organization/api-keys`
- **OpenAI API Quickstart**: `https://platform.openai.com/docs/quickstart`
- **Chat Completions 레퍼런스**: `https://developers.openai.com/api/reference/resources/chat/subresources/completions`
- **Responses API 가이드(신규 권장)**: `https://developers.openai.com/api/docs/guides/responses-vs-chat-completions`

> 2026 기준으로 **`/v1/chat/completions`도 계속 지원**되지만, 새 프로젝트는 **`/v1/responses`** 사용이 권장됩니다(기능 확장/에이전트 프리미티브 등).

---

## 1) 프로젝트 생성

권장 버전:
- Spring Boot **3.x**
- Java **17+**
- Gradle 또는 Maven

필수 Dependency:
- **Spring Web**

선택 Dependency:
- Lombok
- Spring Boot DevTools

추가 권장(유효성 검사):
- **Validation** (`spring-boot-starter-validation`)

---

## 2) OpenAI API Key 발급

1) `https://platform.openai.com/` 접속 후 로그인  
2) API Key 페이지 이동: `https://platform.openai.com/settings/organization/api-keys`  
3) **Create new secret key** → 생성 후 **즉시 복사**

### ⚠️ 보안 주의(중요)

- **절대 GitHub에 올리면 안 됩니다.**
- `application.yml`에 `sk-...`를 하드코딩하지 마세요.
- 권장 방식:
  - 환경변수(`OPENAI_API_KEY`)로 넣고
  - `application.yml`에서는 `${OPENAI_API_KEY}`로 참조

PowerShell 예시(영구 저장):

```bash
setx OPENAI_API_KEY "sk-xxxx..."
```

> 터미널을 새로 열어야 적용됩니다.

---

## 3) application.yml 설정 (핵심)

아래는 **예시**입니다. (키는 환경변수로 주입)

```yaml
server:
  port: 8080

spring:
  application:
    name: openai-chatbot

openai:
  api-key: ${OPENAI_API_KEY}
  # (기존 방식) Chat Completions
  url: https://api.openai.com/v1/chat/completions
  # (신규 권장) Responses API를 쓰면 아래로 바꿉니다.
  # url: https://api.openai.com/v1/responses
```

---

## 4) OpenAI 요청 DTO 만들기

Chat Completions용 요청 DTO 예시:

```java
package com.example.chatbot.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatRequest {
    private String model;
    private List<Message> messages;

    @Data
    public static class Message {
        private String role;     // user or assistant or system
        private String content;  // 질문 내용
    }
}
```

---

## 5) OpenAI 응답 DTO 만들기

```java
package com.example.chatbot.dto;

import lombok.Data;
import java.util.List;

@Data
public class ChatResponse {
    private List<Choice> choices;

    @Data
    public static class Choice {
        private Message message;
    }

    @Data
    public static class Message {
        private String role;
        private String content;
    }
}
```

---

## 6) Controller (핵심 코드)

아래 코드는 **가장 단순하게 연결하는 예시**입니다.

> 참고: `RestTemplate`는 “여전히 쓸 수는 있지만” 신규 개발에서는 `RestClient`(Spring 6.1+)나 `WebClient` 권장을 많이 합니다.  
> 이 문서는 질문에서 주신 코드 흐름을 유지합니다.

```java
package com.example.chatbot.controller;

import com.example.chatbot.dto.ChatRequest;
import com.example.chatbot.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.Collections;

@RestController
@RequiredArgsConstructor
public class ChatController {

    @Value("${openai.api-key}")
    private String apiKey;

    @Value("${openai.url}")
    private String apiUrl;

    @PostMapping("/chat")
    public String chat(@RequestBody String userMessage) {

        RestTemplate restTemplate = new RestTemplate();

        // 메시지 생성
        ChatRequest.Message message = new ChatRequest.Message();
        message.setRole("user");
        message.setContent(userMessage);

        ChatRequest request = new ChatRequest();
        request.setModel("gpt-4o-mini");
        request.setMessages(Collections.singletonList(message));

        // 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        // API 호출
        ResponseEntity<ChatResponse> response =
                restTemplate.exchange(apiUrl, HttpMethod.POST, entity, ChatResponse.class);

        // 응답 반환
        return response.getBody()
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}
```

---

## 7) 요청 / 응답 테스트

요청:
- `POST /chat`
- `Content-Type: application/json`
- Body:

```json
"안녕? 자기소개 해줘"
```

응답 예:
- `"안녕하세요! 저는 OpenAI가 만든 AI입니다. ..."`

테스트 도구:
- Postman
- curl

---

## 8) (추가) Responses API로 시작하려면?

새 프로젝트는 `https://api.openai.com/v1/responses` 사용이 권장될 수 있습니다.  
가이드: `https://developers.openai.com/api/docs/guides/responses-vs-chat-completions`

차이 요약:
- Chat Completions: `messages`
- Responses: `input` / `instructions` 형태로 더 깔끔한 구조

---

## 9) 다음 편 예고

- 실제 챗봇 API 구성(입력 DTO/응답 DTO 정리)
- 예외 처리(HTTP 401/429/5xx)
- 로그/사용량 관리(확장)

