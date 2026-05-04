# crud2-patterns — `crud2` CRUD를 4가지 객체 조립 방식으로 분리

원본 **`crud2`**(`spring1/crud2`)와 동일한 URL·Thymeleaf 화면을 유지하면서, **엔티티(`DoIt`)를 만드는 방식**만 나눈 Gradle 멀티 모듈입니다.

| 모듈 | 포트 | H2 파일 DB (로컬) | 패턴 |
|------|------|-------------------|------|
| `crud2-pattern-setter` | 9201 | `~/test_crud2_setter` | `new DoIt()` + setter, 수정도 setter |
| `crud2-pattern-constructor` | 9202 | `~/test_crud2_constructor` | `new DoIt(null, title, content)` / 수정은 `new DoIt(num, …)` 저장 (원본과 동일 계열) |
| `crud2-pattern-builder` | 9203 | `~/test_crud2_builder` | Lombok `@Builder` — 서비스에서 `DoIt.builder()...build()` |
| `crud2-pattern-mapper` | 9204 | `~/test_crud2_mapper` | `DoMapper.toNewEntity` / `toEntityWithId`, DTO에는 `toEntity()` 없음 |

## 문서 (`docs/`)

- 네 가지 패턴 한눈에: `docs/네가지-패턴-정리.md` / `docs/four-patterns-summary.md` (동일 내용)
- Mapper vs 나머지 패턴: `docs/MAPPER-다른패턴과-차이.md` (한글) / `docs/MAPPER-vs-other-patterns.md` (영문, 동일 내용)  
  전체 경로 예: `D:\spring1\crud2-patterns\docs\MAPPER-vs-other-patterns.md`

## 실행

```text
cd crud2-patterns
.\gradlew.bat :crud2-pattern-setter:bootRun
```

다른 모듈은 `:crud2-pattern-constructor:bootRun` 등으로 바꿉니다.

## 테스트

```text
.\gradlew.bat test
```

## 원본과의 차이

- 원본 `DoDto.toEntity()`는 **setter/생성자/builder** 모듈에서 제거했습니다(변환은 서비스 또는 Mapper에만).
- **Mapper** 모듈만 `support/DoMapper` 로 DTO→엔티티 변환을 모읍니다.
- **Builder** 모듈은 **`DoService` 안에서 `DoIt.builder()`** 만 사용합니다(별도 Mapper 클래스 없음).
- **Setter** 모듈의 수정은 JPA **영속 엔티티에 setter**로 반영합니다(원본은 merge용 `new DoIt` 저장).
