# spring-chatbot

Spring Boot에서 OpenAI(ChatGPT) API를 호출하는 **최소 동작 예제**입니다.

## 1) 준비물

- Java 17+
- OpenAI API Key

## 2) 환경변수 설정 (Windows PowerShell)

```bash
setx OPENAI_API_KEY "sk-xxxxxxxxxxxxxxxxxxxxxxxx"
```

새 터미널을 열어 적용 후 실행하세요.

## 3) 실행

```bash
cd e:\spring1\spring-chatbot
.\gradlew.bat bootRun
```

서버: `http://localhost:8080`

## 4) 테스트

### 요청

- `POST /chat` (JSON 객체)
- `Content-Type: application/json`

```json
{
  "message": "안녕? 자기소개 해줘"
}
```

또는 더 간단히:

- `POST /chat/text` (문자열)
- `Content-Type: text/plain`

```text
안녕? 자기소개 해줘
```

### 응답

```json
{
  "content": "..."
}
```

## 5) 엔드포인트/구성

- `POST /chat`: OpenAI 호출
- `GET /actuator/health`: 헬스 체크

## 보안 주의

- API Key는 **코드/설정 파일에 하드코딩하지 말고** 환경변수로 넣으세요.

