# spring-chatbot

Spring Boot에서 OpenAI(ChatGPT) API를 호출하는 **최소 동작 예제**입니다.

## 문서

- [OpenAI 연동 가이드 (준비물~Responses·다음 편 로드맵)](docs/OpenAI-연동-가이드.md)
- [소스 구조 설명 (파일별 묶음)](docs/설명.md)

## 1) 준비물

- Java 17+
- OpenAI API Key

## 2) 환경변수 설정 (Windows PowerShell)

### 현재 터미널에만 즉시 적용(권장: 테스트용)

```bash
$env:OPENAI_API_KEY="sk-xxxxxxxxxxxxxxxxxxxxxxxx"
```

### PC에 영구 저장(새 터미널부터 적용)

```bash
setx OPENAI_API_KEY "sk-xxxxxxxxxxxxxxxxxxxxxxxx"
```

새 터미널을 열어 적용 후 실행하세요.

## 3) 실행

```bash
cd e:\spring1\spring-chatbot
.\gradlew.bat bootRun
```

서버: `http://localhost:8081`

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

