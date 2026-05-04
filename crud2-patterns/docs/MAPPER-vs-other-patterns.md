# Mapper vs other three patterns (`crud2-patterns`)

Same CRUD; the main difference is **where DTO → `DoIt` conversion lives**.

| Module | Where conversion happens | Example |
|--------|--------------------------|---------|
| **setter** | Inside `DoService`, **setters** | `new DoIt()` then `setTitle` / `setContent` |
| **constructor** | Inside `DoService`, **constructor** | `new DoIt(null, dto.getTitle(), ...)` |
| **builder** | Inside `DoService`, **Lombok `@Builder`** | `DoIt.builder().title(...).content(...).build()` |
| **mapper** | **`support/DoMapper`** (static) | `DoMapper.toNewEntity(dto)` |

## Mapper-only pieces

- **`DoMapper`**: `toNewEntity(DoDto)`, `toEntityWithId(DoDto)` — `final` class, private ctor, static methods only.
- **`DoService`**: `doRepository.save(DoMapper.toNewEntity(dto))` — no `new DoIt(...)` text in the service.

## Builder vs Mapper

| | Builder | Mapper |
|---|---------|--------|
| Syntax | `DoIt.builder()...build()` | `new DoIt(...)` **inside** `DoMapper` |
| Location | Full chain visible in **service** | Hidden in **`DoMapper`** |
| Lombok | `@Builder` on entity | Entity can stay **constructor-only** |

This repo’s `DoMapper` does **not** use Lombok Builder (you could, inside the mapper, without changing the idea).

## Constructor vs Mapper

Same **`new DoIt(...)`** style; mapper moves that call **out of** `DoService` into `DoMapper`.

## Setter vs Mapper

- **Setter**: often mutates the **loaded** entity with setters.
- **Mapper**: builds a **new** `DoIt` and `save`s (same merge style as constructor module).

## `DoDto`

All four modules omit **`toEntity()`** on the DTO; mapper uses **`DoMapper` only**.

## One-line summary

**Mapper** = collect “build entity from DTO” in **`DoMapper`**; **setter / constructor / builder** keep that logic **inside the service** in their own style.

---

(Korean copy: `MAPPER-다른패턴과-차이.md` in the same folder.)
