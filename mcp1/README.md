# mcp1 — Spring AI 실무 데모

Spring AI 1.0 / Spring Boot 3.5 기반으로 **Tool Calling**, **Structured Output**, **Chat Memory**, **Multimodality**, **MCP 클라이언트(의존성 포함)** 를 한 프로젝트에서 다루는 예제입니다.

## 사전 준비

- JDK 21
- OpenAI 호환 API 키 (`OPENAI_API_KEY` 환경 변수 권장)

## 실행

```bash
export OPENAI_API_KEY=sk-...
./gradlew bootRun
```

Windows (PowerShell):

```powershell
$env:OPENAI_API_KEY="sk-..."
.\gradlew.bat bootRun
```

## API 예시

| 메서드 | 경로 | 설명 |
| --- | --- | --- |
| POST | `/api/ai/memory` | `conversationId`, `message` — 대화 메모리 유지 |
| POST | `/api/ai/tools` | `message` — 주문 조회 Tool Calling |
| POST | `/api/ai/structured` | `topic` — `entity()` 기반 구조화 출력 |
| POST | `/api/ai/structured-converter` | `topic` — `BeanOutputConverter` 명시 사용 |
| POST | `/api/ai/multimodal` | `imageUrl`, `prompt` — 이미지+텍스트 |

## MCP

기본값은 `spring.ai.mcp.client.enabled=false` 입니다. MCP 서버를 붙일 때만 `true`로 바꾸고 [Spring AI MCP Client 문서](https://docs.spring.io/spring-ai/reference/api/mcp/mcp-client-boot-starter-docs.html)에 따라 연결 정보를 설정하세요.

## 과정 소개 문서

교육기관용 과정 소개서(한글)는 `docs/COURSE_INTRODUCTION.md` 를 참고하세요.
