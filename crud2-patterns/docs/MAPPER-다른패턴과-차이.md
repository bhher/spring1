# Mapper 패턴 vs 나머지 세 패턴 — `crud2-patterns` 기준

**파일 위치(전체 경로):** `D:\spring1\crud2-patterns\docs\MAPPER-다른패턴과-차이.md`  
한글 파일이 안 보이면 같은 내용의 **`MAPPER-vs-other-patterns.md`** 를 여세요.

같은 CRUD(목록·상세·등록·수정·삭제)를 두고, **DTO → `DoIt` 엔티티를 만드는 코드를 어디에 두느냐**가 가장 큰 차이입니다.

| 모듈 | 변환(조립) 위치 | 대표 코드 |
|------|------------------|-----------|
| **setter** | `DoService` 안, **setter** | `new DoIt()` → `setTitle` / `setContent` |
| **constructor** | `DoService` 안, **생성자** | `new DoIt(null, dto.getTitle(), …)` |
| **builder** | `DoService` 안, **Lombok Builder** | `DoIt.builder().title(…).content(…).build()` |
| **mapper** | **`support/DoMapper`** (static) | `DoMapper.toNewEntity(dto)` |

---

## 1. Mapper 모듈만 있는 것

### 1.1 전용 클래스 `DoMapper`

**파일:** `crud2-pattern-mapper/.../support/DoMapper.java`

- `toNewEntity(DoDto dto)` — 신규: `new DoIt(null, title, content)`
- `toEntityWithId(DoDto dto)` — 수정용: `new DoIt(num, title, content)`
- `final` 클래스 + `private` 생성자 → **static 메서드만** 쓰는 유틸 형태

### 1.2 서비스는 “흐름”만

**파일:** `.../mapper/service/DoService.java`

```java
return doRepository.save(DoMapper.toNewEntity(dto));
// ...
doRepository.save(DoMapper.toEntityWithId(dto));
```

**`new DoIt(...)` 문자열이 서비스에 없음** — 변환 규칙이 바뀌면 `DoMapper`만 고치면 됩니다.

---

## 2. Builder 모듈과의 차이 (둘 다 “조립”이지만)

| 구분 | Builder | Mapper |
|------|---------|--------|
| 조립 문법 | `DoIt.builder()...build()` | `new DoIt(...)` (생성자) — **Mapper 안에서** |
| 위치 | **서비스**에 빌더 체인이 그대로 드러남 | **`DoMapper`** 로 숨김 |
| 장점 | 읽기 쉬운 체인, 필드 많을 때 유리 | **변환 규칙 한 파일**에 모음, 서비스가 짧아짐 |
| Lombok | 엔티티에 `@Builder` 필요 | 엔티티는 **순수 생성자**만 있어도 됨 |

Mapper 모듈의 `DoMapper`는 **Lombok Builder를 쓰지 않습니다.** (원하면 `DoMapper` 안에서 `DoIt.builder()` 로 바꿀 수는 있음 — 역할은 동일하게 “변환 전담”.)

---

## 3. Constructor 모듈과의 차이

- **생성자** 모듈: `DoService` 안에 직접 `new DoIt(...)`.
- **Mapper** 모듈: **같은 `new DoIt(...)`** 이지만 **`DoMapper`로 이동**.

즉 **엔티티 생성 방식(생성자)** 은 같고, **그 호출을 서비스에서 분리했는지**가 다릅니다.

---

## 4. Setter 모듈과의 차이

- **Setter:** 조회 후 **영속 엔티티에 `setTitle` / `setContent`** (또는 신규 `new` + setter).
- **Mapper:** **항상 새 `DoIt` 인스턴스**를 만들어 `save` (원본·생성자 모듈과 같은 merge 스타일).

수정 시 “같은 행을 수정” vs “새 객체로 merge”는 **Setter만** 다르고, **Mapper와 Constructor는 같은 계열**입니다.

---

## 5. DTO `DoDto`

네 모듈 모두 **`toEntity()` 메서드는 없음** (원본 `crud2`의 `DoDto.toEntity()` 제거).

- **Mapper:** 변환은 오직 `DoMapper` static.
- **나머지:** 서비스 안에서 직접 조립.

---

## 6. 언제 Mapper를 쓰나 (실무 감각)

- DTO ↔ 엔티티 매핑이 **여러 서비스·여러 API**에서 반복될 때  
- 필드가 많아져 **`DoService`가 길어질 때**  
- 나중에 **MapStruct** 등으로 바꾸기 쉽게 **변환 경계**를 고정하고 싶을 때  

소규모 예제 한 곳만 쓰면 **서비스에 직접 Builder/생성자**만으로도 충분한 경우가 많습니다.

---

## 7. 요약 한 줄

**Mapper = “DTO에서 엔티티 만드는 코드”를 `DoMapper` 한곳으로 모은 것**이고, **Setter / Constructor / Builder** 는 그 변환을 **서비스 안에서** 각각의 스타일로 처리합니다.
