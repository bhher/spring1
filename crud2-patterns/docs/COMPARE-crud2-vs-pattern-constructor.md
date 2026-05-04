# `crud2` 와 `crud2-pattern-constructor` 비교

원본 **`crud2`**와 **`crud2-patterns`** 안의 **`crud2-pattern-constructor`** 모듈이 **어디가 바뀌었는지**, **동작·의도 차이**를 정리합니다.

| 구분 | 경로 |
|------|------|
| 원본 | `spring1/crud2/` |
| 생성자 패턴 모듈 | `spring1/crud2-patterns/crud2-pattern-constructor/` |

---

## 1. 프로젝트 구조

| 항목 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| 빌드 | 단일 Gradle 프로젝트 | **`crud2-patterns`** 멀티 모듈의 **하위 모듈** |
| 실행 클래스 | `com.example.crud2.Crud2Application` | `com.example.crud2.constructor.Crud2ConstructorApplication` |
| 기본 패키지 | `com.example.crud2` | `com.example.crud2.constructor` (하위에 `entity`, `dto`, `service` … 동일 구성) |

---

## 2. 변경된 파일·위치 (역할별)

### 2.1 엔티티 `DoIt`

| 항목 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| 패키지 | `com.example.crud2.entity` | `com.example.crud2.constructor.entity` |
| Lombok | `@Getter`, `@NoArgsConstructor`, **`@AllArgsConstructor`** | `@Getter`, `@NoArgsConstructor(access = PROTECTED)`, **명시적 `public DoIt(Long num, String title, String content)`** |
| 의도 | Lombok이 전체 인자 생성자 생성 | **생성자가 소스에 보이도록** (학습용) |

동작상으로는 여전히 **`new DoIt(null, title, content)`** 형태로 저장 가능합니다.

### 2.2 DTO `DoDto`

| 항목 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| 패키지 | `com.example.crud2.dto` | `com.example.crud2.constructor.dto` |
| **`toEntity()`** | **있음** (`new DoIt(num, title, content)`) | **없음** — DTO→엔티티는 **서비스**에서만 처리 |

### 2.3 서비스 `DoService`

| 항목 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| 패키지 | `com.example.crud2.service` | `com.example.crud2.constructor.service` |
| `create` / `update` 로직 | `new DoIt(null, …)` / `new DoIt(dto.getNum(), …)` | **동일** |

즉 **비즈니스 흐름은 같고**, DTO 안의 `toEntity()`만 빼고 서비스에만 두었습니다.

### 2.4 컨트롤러 `DoController`

| 항목 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| URL·파라미터·뷰 이름 | 동일 (`/list`, `/mains/create` 등) | 동일 |
| 변경점 | — | **import 패키지**만 `constructor` 기준으로 변경 |

### 2.5 설정 `application.properties`

| 항목 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| `spring.application.name` | `crud2` | `crud2-pattern-constructor` |
| **`server.port`** | 없음(기본 **8080**) | **`9202`** (원본과 동시 실행 가능) |
| H2 URL | `jdbc:h2:~/test_crud2` | `jdbc:h2:~/test_crud2_constructor` (**DB 파일 분리**) |
| `spring.jpa.show-sql` | `true` | `false` |
| `app.pattern-label` | 없음 | `생성자` (실습용, 템플릿에서 쓰려면 확장 가능) |

### 2.6 화면(Thymeleaf)

- **constructor** 모듈은 `crud2`의 `templates`를 **복사**해 사용합니다.  
- 내용은 동일에 가깝고, **앱 이름·포트**만 위 설정에 따라 달라집니다.

### 2.7 빌드 스크립트

| 항목 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| `build.gradle` | 단일 모듈 의존성 전체 정의 | **루트 `crud2-patterns/build.gradle`** 의 `subprojects { … }` 를 상속, 모듈 폴더에는 빈 마커 `build.gradle`만 둠 |

---

## 3. 차이점 요약 (한눈에)

| 구분 | crud2 | crud2-pattern-constructor |
|------|--------|---------------------------|
| 목적 | 단일 CRUD 예제 | **생성자 패턴 실습** + 다른 패턴 모듈과 **나란히 비교** |
| 패키지 | `com.example.crud2` | `com.example.crud2.constructor` |
| `DoDto.toEntity()` | 있음 | **제거** (변환 책임을 DTO 밖으로) |
| `DoIt` 정의 | Lombok `@AllArgsConstructor` | **직접 적은 생성자** + `protected` 기본 생성자 |
| 포트 / DB 파일 | 8080 / `test_crud2` | **9202** / `test_crud2_constructor` |
| SQL 로그 | 켜짐 | 끔(기본) |

---

## 4. 같게 둔 것

- URL 설계, `DoRepository`, `create` / `update` / `delete` / `findAll` / `findById`의 **처리 순서와 결과**  
- JPA `ddl-auto=create`, H2 콘솔 사용 여부(켜 둠)

---

## 5. 실행 시 참고

- **동시에 띄우려면** 포트와 H2 URL이 다르므로 **충돌하지 않습니다.**  
- `crud2-pattern-constructor`만 실행하려면 저장소 루트에서:

```text
cd spring1/crud2-patterns
.\gradlew.bat :crud2-pattern-constructor:bootRun
```
