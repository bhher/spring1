# hospital-reservation

병원 예약 시스템(연습용) 예제입니다.

## 핵심 기능

- 예약 생성 / 취소
- 시간대(슬롯) 선택
- 중복 예약 방지(동시성): `AppointmentSlot`의 `@Version`(낙관락) + 재시도
- 스케줄링(데모): 예약 리마인더를 주기적으로 로그로 출력

## 실행

### 백엔드

```bash
cd e:\spring1\hospital-reservation
.\gradlew.bat bootRun
```

- H2 파일 DB: `~/hospital_reservation_db`
- H2 콘솔: `http://localhost:8080/h2-console`

### 프론트(React)

```bash
cd e:\spring1\hospital-reservation\frontend
npm install
npm run dev
```

브라우저: `http://localhost:5173`

## API 요약

- `GET /api/doctors`
- `GET /api/doctors/{doctorId}/slots?date=2026-04-28`
- `POST /api/reservations` (body: `{ slotId, patientName, patientPhone }`)
- `POST /api/reservations/cancel` (body: `{ reservationId }`)
- `GET /api/reservations`