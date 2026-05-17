# OpenAI 연동 가이드 (Spring Boot + Chat Completions)

이 문서는 **처음부터 따라 하기**용 개요와, 본 저장소(`spring-chatbot`) 실제 코드와의 **차이점**을 함께 적습니다.

---

## 1) 준비물 · 의존성

### 실행 환경

- **Java 17+**
- **Gradle** 또는 **Maven** (본 프로젝트는 Gradle)

### 필수 Dependency

- **Spring Web** (`spring-boot-starter-web`) — REST 컨트롤러, JSON

### 선택 Dependency

- **Lombok** — `@Data`, `@RequiredArgsConstructor` 등 보일러플레이트 감소
- **Spring Boot DevTools** — 개발 시 자동 재시작 등 (선택)

### 추가 권장 (유효성 검사)

- **Validation** (`spring-boot-starter-validation`) — `@Valid`, `@NotBlank` 등

본 저장소 `build.gradle`에는 Web, Validation, Actuator, Lombok이 포함되어 있습니다.

---

## 2) OpenAI API Key 발급

1. [https://platform.openai.com/](https://platform.openai.com/) 접속 후 로그인  
2. API Key 페이지: [Organization API keys](https://platform.openai.com/settings/organization/api-keys)  
3. **Create new secret key** → 생성 직후 **한 번만** 표시되므로 즉시 복사  

### 보안 주의 (중요)

- **GitHub 등 원격 저장소에 `sk-...` 커밋 금지**  
- `application.yml`에 API 키를 **하드코딩하지 마세요**  

### 권장 방식

- 환경 변수 **`OPENAI_API_KEY`** 로 주입  
- YAML에서는 **`${OPENAI_API_KEY}`** 또는 기본값 없이 강제: `${OPENAI_API_KEY}`  

**PowerShell — 영구 저장 (`setx`):**

```powershell
setx OPENAI_API_KEY "sk-xxxx..."
```

- **새 터미널**을 열어야 반영됩니다.  
- 현재 세션만 쓰려면: `$env:OPENAI_API_KEY="sk-..."`  

본 저장소는 `application.yml`에서 다음과 같이 읽습니다 (기본값 빈 문자열은 로컬 편의용이며, 운영에서는 환경 변수 필수 권장).

```yaml
openai:
  api-key: ${OPENAI_API_KEY:}
```

---

## 3) application.yml 설정 (핵심)

### 예시 A — URL 한 덩어리 + 포트 8080 (가이드용)

키는 **환경 변수**로만 주입합니다.

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

컨트롤러에서 `@Value("${openai.url}")` 로 POST 대상 전체 URL을 쓰는 방식과 잘 맞습니다.

### 예시 B — 본 저장소 `spring-chatbot` 실제 구조

호스트와 경로를 나누고, 모델명도 설정으로 둡니다. 포트는 **8081**입니다.

```yaml
server:
  port: 8081

spring:
  application:
    name: spring-chatbot

openai:
  api-key: ${OPENAI_API_KEY:}
  base-url: https://api.openai.com/v1
  chat-completions-path: /chat/completions
  model: gpt-4o-mini
```

`OpenAiProperties` record와 `OpenAiClient`(RestClient)가 위 값을 사용합니다.

---

## 4) OpenAI 요청 DTO 만들기 (Chat Completions)

### Lombok `@Data` 스타일 (가이드 예시)

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
        private String role;     // user, assistant, system
        private String content;
    }
}
```

### 본 저장소 방식

`openai.dto.ChatCompletionsRequest` — **Java record** + 중첩 record `Message` 로 동일한 JSON 구조를 표현합니다. (Lombok 없이도 직렬화 가능)

---

## 5) OpenAI 응답 DTO 만들기

### Lombok `@Data` 스타일 (가이드 예시)

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

### 본 저장소 방식

`openai.dto.ChatCompletionsResponse` — record로 `choices → message → content` 경로만 맞추면, OpenAI가 내려주는 **나머지 JSON 필드는 무시**됩니다.

---

## 6) Controller (핵심 코드)

### 참고: RestTemplate vs RestClient / WebClient

- **RestTemplate** — 여전히 사용 가능하나, **신규 코드**에서는 유지보수·API 관점에서 **RestClient**(Spring 6.1+) 또는 **WebClient**(리액티브) 권장이 많습니다.  
- 본 저장소는 **`RestClient`** + 별도 `OpenAiClient` 컴포넌트로 HTTP 호출을 분리합니다.

### RestTemplate 예시 (질문에서 주신 흐름 유지)

가장 단순하게 한 클래스에 붙이는 형태입니다.

```java
package com.example.chatbot.controller;

import com.example.chatbot.dto.ChatRequest;
import com.example.chatbot.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

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

        ChatRequest.Message message = new ChatRequest.Message();
        message.setRole("user");
        message.setContent(userMessage);

        ChatRequest request = new ChatRequest();
        request.setModel("gpt-4o-mini");
        request.setMessages(Collections.singletonList(message));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<ChatRequest> entity = new HttpEntity<>(request, headers);

        ResponseEntity<ChatResponse> response =
                restTemplate.exchange(apiUrl, HttpMethod.POST, entity, ChatResponse.class);

        return response.getBody()
                .getChoices()
                .get(0)
                .getMessage()
                .getContent();
    }
}
```

### 본 저장소와의 대응

| 가이드 예시 | 본 저장소 |
|-------------|-----------|
| `@Value` 로 키·URL | `OpenAiProperties` (`@ConfigurationProperties`) |
| `RestTemplate` | `OpenAiClient` + `RestClient` |
| 응답이 `String` | API용 `ChatResponseDto` JSON `{"content":"..."}` + 검증용 `POST /chat` |

---

## 7) 요청 / 응답 테스트

### 가이드 예시와 동일한 스타일 (`POST /chat` + 문자열 body)

- **요청:** `POST /chat`  
- **Content-Type:** `application/json`  
- **Body:** `"안녕? 자기소개 해줘"` (JSON 문자열)

### 응답 예 (문자열 그대로 반환하는 컨트롤러인 경우)

```text
"안녕하세요! 저는 OpenAI가 만든 AI입니다. ..."
```

### 본 저장소 실제 엔드포인트

| 메서드 | 경로 | Body | 응답 |
|--------|------|------|------|
| POST | `/chat` | `{"message":"..."}` | `{"content":"..."}` |
| POST | `/chat/text` | plain 문자열 | 동일 |

**테스트 도구:** Postman, curl, IntelliJ HTTP Client 등.

**curl 예 (`/chat`):**

```bash
curl -s http://localhost:8081/chat -H "Content-Type: application/json" -d "{\"message\":\"안녕\"}"
```

---

## 8) (추가) Responses API로 시작하려면?

신규 프로젝트는 **`https://api.openai.com/v1/responses`** 사용이 권장될 수 있습니다.

- 가이드: [Responses vs Chat Completions](https://developers.openai.com/api/docs/guides/responses-vs-chat-completions)

### 차이 요약

| 항목 | Chat Completions | Responses |
|------|------------------|-----------|
| 요청 본문 | `messages` 배열 중심 | `input`, `instructions` 등 구조화 |
| 용도 | 기존 챗 UI·예제와 호환 쉬움 | 에이전트·툴 호출 등 최신 API 설계 |

본 저장소는 **Chat Completions** 기준입니다. Responses로 바꿀 경우 `OpenAiClient`의 URL·DTO·파싱 경로를 전부 Responses 스펙에 맞게 교체해야 합니다.

---

## 9) 다음 편 예고 (로드맵)

아래는 **이어서 다루면 좋은 주제**를 “편” 단위로 나눈 것입니다. 각 편은 독립적으로 읽어도 되고, 순서대로 확장하면 실제 서비스에 가까워집니다.

### 제10편 — REST API 설계 다듬기 (입력/출력 DTO 정리)

- **요청 DTO:** 단일 `String` 대신 `record`/`class`로 필드 명시 (`message`, 옵션으로 `model`, `temperature` 등).  
- **응답 DTO:** `{ "content" }` 외에 `model`, `usage`(토큰), `id` 등 **프론트·로그에 필요한 필드만** 노출할지 정책 결정.  
- **버전 prefix:** `/api/v1/chat` 처럼 URL 규칙 통일.  
- 본 저장소는 이미 `ChatRequestDto`, `ChatResponseDto`, `@Valid` 가 일부 반영되어 있음 → 필드 확장·문서화(SpringDoc OpenAPI)로 이어가기 좋음.

### 제11편 — 예외 처리·복원력 (HTTP 401 / 429 / 5xx)

- **401:** API 키 오타·만료 → 사용자에게 “설정 오류” 메시지, 서버 로그에는 키 미노출.  
- **429:** Rate limit → `Retry-After` 헤더 확인, 백오프 재시도 또는 큐잉 설계.  
- **5xx:** OpenAI 측 일시 장애 → 타임아웃·서킷 브레이커(Resilience4j)·사용자에게 “잠시 후 재시도”.  
- 본 저장소의 `GlobalExceptionHandler`를 기준으로 **OpenAI 전용 예외 타입**으로 세분화하거나, `RestClient`의 `onStatus`에서 매핑하는 편이 깔끔함.

### 제12편 — 로그·관측·사용량

- **구조화 로그:** 요청 ID(traceId), 모델명, 소요 시간(ms), **토큰 usage**만 남기기(본문 전문은 개인정보·용량 이슈로 제한).  
- **Micrometer / Actuator:** 메트릭·헬스 연동(이미 Actuator 의존성 있음).  
- **비용:** 월별 토큰 집계를 DB 또는 간단 파일로 적재하는 배치 아이디어.

### 제13편 — 대화 맥락(멀티턴)·시스템 프롬프트 외부화

- 세션별 `List<Message>` 유지(메모리 vs Redis vs DB).  
- 시스템 지시문을 `application.yml` 또는 DB에서 읽어 **배포 없이** 조정.  
- 토큰 한도 초과 시 오래된 메시지 잘라내기(sliding window).

### 제14편 — 스트리밍(SSE) / WebClient

- 채팅 UI에 **토큰 단위 스트리밍**이 필요하면 Chat Completions `stream=true` + SSE 처리.  
- **WebClient**로 전환 시 논블로킹 파이프라인·백프레셔 설계 연습.

### 제15편 — Responses API·툴 호출(함수 calling) 입문

- 공식 가이드에 맞춰 요청/응답 DTO 교체.  
- 날씨·DB 조회 등 **도구**를 붙이는 패턴은 “챗봇”에서 “에이전트”로 확장되는 지점.

---

## 문서 간 링크

- 이 저장소 **패키지·클래스 역할** 요약: [설명.md](./설명.md)  
- 실행·환경 변수: 프로젝트 루트 [README.md](../README.md)
