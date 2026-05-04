# Builder 방식 vs 생성자 방식 — 차이 정리 (`crud2-patterns`)

이 문서는 **`crud2-pattern-builder`** 와 **`crud2-pattern-constructor`** 를 기준으로, **같은 CRUD**를 두 패턴으로 나눴을 때 **무엇이 다른지** 개념과 코드 양쪽에서 비교합니다.

| 모듈 | 경로 |
|------|------|
| 생성자 | `crud2-patterns/crud2-pattern-constructor/` |
| Builder (Lombok) | `crud2-patterns/crud2-pattern-builder/` |

---

## 1. 한 줄로 말하면

- **생성자:** `new DoIt(null, title, content)` — **인자 순서**를 맞춰야 하고, “두 번째가 뭐지?”가 코드만 봐서는 덜 드러날 수 있음.  
- **Builder:** `DoIt.builder().title(...).content(...).build()` — **이름이 붙은 인자**처럼 읽혀서 필드가 늘어날수록 유리한 경우가 많음.

결국 Builder도 `build()` 안에서는 **전체 필드를 받는 생성자(또는 동등한 조립)** 로 객체를 만듭니다. Lombok은 그 보일러플레이트를 생성해 줄 뿐입니다.

---

## 2. 이 프로젝트에서의 코드 차이

### 2.1 엔티티 `DoIt` 정의

| 항목 | 생성자 모듈 | Builder 모듈 |
|------|-------------|----------------|
| 기본 생성자 | `protected` 무인자 (JPA용) | `protected` 무인자 (`@NoArgsConstructor`) |
| 값 주입 | **`public DoIt(Long num, String title, String content)`** 직접 작성 | **`@Builder`** + `@AllArgsConstructor(access = PRIVATE)` — 외부에서 `new DoIt(...)` 호출 대신 빌더 사용 유도 |
| Lombok | `@Getter`, `@NoArgsConstructor` | `@Getter`, `@NoArgsConstructor`, `@AllArgsConstructor`, **`@Builder`** |

### 2.2 서비스 `create`

**생성자**

```java
DoIt entity = new DoIt(null, dto.getTitle(), dto.getContent());
```

**Builder**

```java
DoIt entity = DoIt.builder()
        .title(dto.getTitle())
        .content(dto.getContent())
        .build();
```

`num` 은 둘 다 신규라 **`null`** 이 들어갑니다. Builder 쪽은 **`.num(null)` 을 생략**해도 됩니다.

### 2.3 서비스 `update`

이 레포에서는 **둘 다** “`findById` 후 **새 `DoIt` 인스턴스**를 만들어 `save`” 패턴으로 맞춰 두었습니다.

| 생성자 | Builder |
|--------|---------|
| `new DoIt(dto.getNum(), dto.getTitle(), dto.getContent())` | `DoIt.builder().num(dto.getNum()).title(...).content(...).build()` |

**동작 의미(merge 후 저장)** 는 같고, **표기만** Builder가 더 읽기 쉬운 형태입니다.

---

## 3. 개념 비교표

| 구분 | 생성자 | Builder (Lombok 등) |
|------|--------|----------------------|
| 읽기 | `(null, title, content)` — 위치에 의미 | `.title(x).content(y)` — **이름**으로 의미 |
| 필드 증가 | 인자 많아지면 실수·가독성 부담 | 체인으로 **선택 필드** 추가가 수월 |
| JPA | `protected` 기본 + **공개(또는 패키지) 생성자** 조합이 흔함 | `@NoArgsConstructor(PROTECTED)` + `@Builder` 조합이 흔함 |
| 도구 의존 | 순수 Java | Lombok(또는 수동 빌더 클래스) |

---

## 4. “뭘 써야 하나?” 실무 감각

- 필드가 **적고(2~3개)** 고정이면 **생성자만**으로도 충분한 경우가 많습니다.  
- 필드가 **많거나** 선택값·기본값이 늘면 **Builder**를 많이 택합니다.  
- 팀에서 Lombok을 쓰지 않으면 **수동 빌더 클래스** 또는 **팩토리 메서드**로 같은 이득을 얻습니다.

---

## 5. 실행 시 구분

| 항목 | 생성자 모듈 | Builder 모듈 |
|------|-------------|--------------|
| 포트 | 9202 | 9203 |
| H2 DB 파일 | `~/test_crud2_constructor` | `~/test_crud2_builder` |
| 앱 이름 | `crud2-pattern-constructor` | `crud2-pattern-builder` |

```text
.\gradlew.bat :crud2-pattern-constructor:bootRun
.\gradlew.bat :crud2-pattern-builder:bootRun
```

---

## 6. 요약

- **생성자**와 **Builder**의 본질 차이는 “**값을 어떻게 모아서 객체를 완성하느냐**”이고, 이 프로젝트에서는 **저장·수정 흐름은 동일**하게 두고 **조립 문법만** 나눴습니다.  
- Builder는 **가독성·확장**에 유리하고, 생성자는 **의존성·단순함**에 유리한 편입니다.
