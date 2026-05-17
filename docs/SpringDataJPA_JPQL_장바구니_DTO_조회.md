# Spring Data JPA `@Query` — 장바구니 상품을 DTO로 바로 조회하는 JPQL

Spring Data JPA에서 `@Query`로 **JPQL**을 작성하고, `select new ...`로 **화면용 DTO**를 곧바로 매핑하는 패턴을 정리한 문서입니다.

---

## 전체 예시 코드

```java
@Query("select new com.shop.dto.CartDetailDto(ci.id, i.itemNm, i.price, ci.count, im.imgUrl) " +
        "from CartItem ci, ItemImg im " +
        "join ci.item i " +
        "where ci.cart.id = :cartId " +
        "and im.item.id = ci.item.id " +
        "and im.repimgYn = 'Y' " +
        "order by ci.regTime desc"
)
List<CartDetailDto> findCartDetailDtoList(Long cartId);
```

---

## 1. `select new` DTO 생성

```text
select new com.shop.dto.CartDetailDto(...)
```

JPQL에서 **생성자 표현식(constructor expression)** 으로 DTO 인스턴스를 바로 만드는 문법입니다. 실행 결과 한 행(row)마다 `new CartDetailDto(...)`가 호출됩니다.

### 생성자에 넘기는 인자

| 순서 | JPQL 인자 | 의미 |
|------|-----------|------|
| 1 | `ci.id` | 장바구니 상품(CartItem) ID |
| 2 | `i.itemNm` | 상품명 |
| 3 | `i.price` | 상품 가격 |
| 4 | `ci.count` | 담은 수량 |
| 5 | `im.imgUrl` | 대표 이미지 URL |

### DTO 생성자 (순서·타입 일치 필수)

```java
public CartDetailDto(Long cartItemId,
                     String itemNm,
                     int price,
                     int count,
                     String imgUrl)
```

**JPQL 인자 순서·타입이 생성자와 정확히 같아야** 합니다. 전체 패키지명(`com.shop.dto.CartDetailDto`)은 JPQL 문자열에 맞춥니다.

---

## 2. `FROM` 절 — 조회 대상 엔티티

```text
from CartItem ci, ItemImg im
```

| 엔티티 | 별칭 | 역할 |
|--------|------|------|
| `CartItem` | `ci` | 장바구니에 담긴 한 줄(상품·수량 등) |
| `ItemImg` | `im` | 상품 이미지 |

쉼표(`,`)로 나열하면 **카티전 곱에 가까운 FROM** 이 되므로, 실제로는 아래 `WHERE`로 **같은 상품·같은 장바구니 조건**을 걸어 의미 있는 조합만 남깁니다.

---

## 3. `JOIN` — 연관관계 조인

```text
join ci.item i
```

`CartItem`에 예를 들어 다음과 같은 매핑이 있다는 전제입니다.

```java
@ManyToOne
@JoinColumn(name = "item_id")
private Item item;
```

- `ci.item`으로 `Item`에 접근하고, 별칭을 `i`로 둡니다.
- SQL로 비유하면 `JOIN item i ON ci.item_id = i.id`와 비슷한 역할입니다.

---

## 4. `WHERE` 조건

### (1) 특정 장바구니만

```text
where ci.cart.id = :cartId
```

- 메서드 인자 `Long cartId`가 `:cartId`에 바인딩됩니다.
- 예: `findCartDetailDtoList(3L)` → 해당 장바구니 ID가 3인 `CartItem`만.

### (2) 이미지가 같은 상품을 가리키도록

```text
and im.item.id = ci.item.id
```

- `ItemImg`의 상품과 `CartItem`이 가리키는 상품이 동일한 행만 남깁니다.
- SQL로는 `item_img.item_id = item.id` (또는 `ci`가 가진 `item_id`)와 같은 취지입니다.

### (3) 대표 이미지만

```text
and im.repimgYn = 'Y'
```

- 여러 이미지 중 **대표 이미지**(`repimgYn = 'Y'`)만 사용합니다.

---

## 5. 정렬

```text
order by ci.regTime desc
```

- **최근에 담은 순**으로 정렬합니다.

---

## 6. 결과로 얻는 데이터 (예시)

| 상품명 | 가격 | 수량 | 이미지 |
|--------|------|------|--------|
| 맨투맨 | 30000 | 2 | aaa.jpg |
| 청바지 | 50000 | 1 | bbb.jpg |

이런 형태가 `List<CartDetailDto>`로 반환됩니다.

---

## 7. 전체 흐름 요약

```text
CartItem
   → Item 조인 (ci.item → i)
   → 대표 ItemImg 연결 (WHERE로 같은 item + repimgYn = 'Y')
   → select new로 CartDetailDto 생성
   → List<CartDetailDto> 반환
```

---

## 8. 대응되는 SQL 느낌

실제 생성 SQL은 벤더·매핑에 따라 다르지만, 개념적으로는 아래와 비슷합니다.

```sql
SELECT
    ci.id,
    i.item_nm,
    i.price,
    ci.count,
    im.img_url
FROM cart_item ci
JOIN item i
    ON ci.item_id = i.id
JOIN item_img im
    ON im.item_id = i.id
WHERE ci.cart_id = ?
  AND im.repimg_yn = 'Y'
ORDER BY ci.reg_time DESC;
```

---

## 9. 왜 엔티티 대신 DTO 조회를 쓰는가?

| 관점 | 설명 |
|------|------|
| **성능** | 필요한 컬럼만 SELECT 하므로, 엔티티 전체 + 지연 로딩 탐색보다 유리한 경우가 많습니다. |
| **쿼리 최적화** | 한 번의 쿼리(또는 명시적 조인)로 화면에 필요한 묶음을 가져오기 쉽습니다. |
| **표현 계층** | 화면 전용 필드만 담아 API·Thymeleaf 등에 바로 넘기기 좋습니다. |

엔티티로만 조회할 때는 `CartItem` → `Item` → `ItemImg`처럼 **연관을 계속 타며** N+1이나 불필요한 로딩이 생기기 쉽고, DTO 프로젝션은 그걸 줄이는 데 자주 쓰입니다.

---

## 참고

- `CartItem`, `Item`, `ItemImg` 클래스명·필드명은 예시 프로젝트(`com.shop`) 기준입니다. 본인 프로젝트 엔티티명에 맞게 JPQL을 수정하면 됩니다.
- `from A a, B b` 형태는 조건을 빼먹으면 **곱집합**이 커질 수 있으므로, `WHERE`로 관계를 반드시 제한하는지 검토하는 것이 좋습니다.
