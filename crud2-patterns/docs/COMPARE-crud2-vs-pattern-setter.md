# `crud2` 와 `crud2-pattern-setter` 비교 — Setter 방식에서 바뀐 것

원본 **`crud2`**와 **`crud2-patterns/crud2-pattern-setter`** 를 비교해, **Setter 패턴**을 적용하면서 **무엇이 달라졌는지** 정리합니다.

| 구분 | 경로 |
|------|------|
| 원본 | `spring1/crud2/` |
| Setter 모듈 | `spring1/crud2-patterns/crud2-pattern-setter/` |

---

## 1. 프로젝트·패키지

| 항목 | crud2 | crud2-pattern-setter |
|------|--------|----------------------|
| 실행 클래스 | `com.example.crud2.Crud2Application` | `com.example.crud2.setter.Crud2SetterApplication` |
| 패키지 루트 | `com.example.crud2` | `com.example.crud2.setter` |
| 빌드 | 단일 프로젝트 | `crud2-patterns` 멀티 모듈 하위 |

---

## 2. 엔티티 `DoIt` — 가장 큰 차이

| 항목 | crud2 | crud2-pattern-setter |
|------|--------|----------------------|
| Lombok | `@Getter`, `@NoArgsConstructor`, **`@AllArgsConstructor`** | `@Getter`, **`@Setter`**, `@NoArgsConstructor` |
| **전 인자 생성자** | Lombok이 `new DoIt(num, title, content)` 제공 | **없음** (setter로만 필드 주입) |
| 의미 | 생성자로 한 번에 조립 가능 | **빈 객체 + setter** 로 조립하는 학습용 모델 |

---

## 3. DTO `DoDto`

| 항목 | crud2 | crud2-pattern-setter |
|------|--------|----------------------|
| `toEntity()` | **있음** (`new DoIt(num, title, content)`) | **없음** |
| 나머지 | `@Getter` `@Setter` 등 동일 계열 | 동일 (필드·Lombok은 같게 둠) |

Setter 모듈에서는 **DTO가 엔티티를 만들지 않고**, 서비스에서 `new DoIt()` 후 setter만 사용합니다.

---

## 4. 서비스 `DoService` — 생성·수정 방식

### 4.1 `create`

| crud2 | crud2-pattern-setter |
|-------|----------------------|
| `new DoIt(null, dto.getTitle(), dto.getContent())` | `DoIt entity = new DoIt();` → `setTitle`, `setContent` → `save` |

### 4.2 `update` (여기서 원본과 **동작 방식**이 갈림)

| crud2 | crud2-pattern-setter |
|-------|----------------------|
| `findById` 후 **`new DoIt(dto.getNum(), dto.getTitle(), dto.getContent())`** 를 만들어 **`save(merged)`** (새 인스턴스 merge) | `findById`로 가져온 **영속 엔티티 `existing`** 에 **`setTitle` / `setContent`** 만 호출 후 **`save(existing)`** |

즉 Setter 모듈은 **“조회한 행을 그대로 수정”**하는 전형적인 setter 업데이트이고, 원본은 **“새 객체로 덮어쓰기”**에 가깝습니다. 둘 다 JPA에서 저장은 되지만, **객체 수명·의도 표현**이 다릅니다.

### 4.3 `findAll` / `findById` / `delete`

로직은 **동일**합니다.

---

## 5. 컨트롤러·리포지토리

- **URL·뷰 이름**은 원본과 동일합니다.
- 변경은 **패키지 import** (`setter` 하위) 정도입니다.
- `DoRepository` 인터페이스 내용은 동일 패턴입니다.

---

## 6. `application.properties`

| 항목 | crud2 | crud2-pattern-setter |
|------|--------|----------------------|
| 앱 이름 | `crud2` | `crud2-pattern-setter` |
| 포트 | 기본 **8080** | **9201** |
| H2 URL | `~/test_crud2` | `~/test_crud2_setter` |
| `show-sql` | `true` | `false` |
| `app.pattern-label` | 없음 | `Setter` |

---

## 7. 요약 표

| 구분 | crud2 | crud2-pattern-setter |
|------|--------|----------------------|
| 엔티티 | 생성자 조립(`@AllArgsConstructor`) | **setter 조립** (`@Setter`, 인자 생성자 없음) |
| DTO `toEntity()` | 있음 | **제거** |
| 생성 | `new DoIt(null, …)` | `new DoIt()` + setter |
| 수정 | `new DoIt(num, …)` 저장 | **조회 엔티티에 setter** |
| 포트 / DB | 8080 / `test_crud2` | 9201 / `test_crud2_setter` |

---

## 8. 실행 예

```text
cd spring1/crud2-patterns
.\gradlew.bat :crud2-pattern-setter:bootRun
```

원본 `crud2`와 **동시에** 띄우려면 위처럼 포트·H2 파일이 분리되어 있어야 합니다.
