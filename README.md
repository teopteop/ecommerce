# E-Commerce Backend

Spring Boot 기반의 이커머스 백엔드 프로젝트입니다.  
도메인 중심 설계(DDD), 애그리게이트 기반 상태 관리, 외부 결제 연동, 동시성 제어를 핵심 구현 목표로 삼았습니다.

---

## Tech Stack

| 분류 | 기술 |
|------|------|
| Language | Java 17 |
| Framework | Spring Boot 4.0 |
| Security | Spring Security + JWT |
| ORM | Spring Data JPA + QueryDSL |
| Database | MySQL, Redis |
| HTTP Client | Spring WebFlux (WebClient) |
| External API | 토스페이먼츠 |
| Build | Gradle |
| Test | JUnit 5, Mockito |

---

## Architecture

### DDD 기반 도메인 패키지 구조

각 도메인은 독립적인 패키지로 분리되며, 공통 관심사는 `global`로 모읍니다.  
도메인 간 참조는 엔티티 객체 직접 참조 대신 **ID 참조**로 결합도를 낮춥니다.  
(예: `Order`는 `Member` 객체를 들고 있지 않고 `memberId`만 보유)

```
com.teopteop.ecommerce
├── domain
│   ├── auth        # 인증 컨텍스트 — 회원가입, 로그인, JWT 발급
│   ├── member      # 회원 컨텍스트 — 프로필, 기본 배송지
│   ├── product     # 상품 컨텍스트 — 상품 등록/조회, 어드민 관리
│   ├── category    # 카테고리 컨텍스트
│   ├── inventory   # 재고 컨텍스트 — 수량 관리, 이력 추적
│   ├── order       # 주문 컨텍스트 — 주문/배송 상태 관리
│   └── payment     # 결제 컨텍스트 — 토스페이먼츠 연동, 상태 전이
└── global
    ├── common      # ApiResponse, PageResponse, BaseEntity, Address VO
    ├── config      # JPA Auditing, QueryDSL, Security, WebClient
    ├── exception   # ApplicationException, BaseErrorCode, GlobalExceptionHandler
    └── security    # JWT 필터, EntryPoint, AccessDeniedHandler
```

### Command / Query 서비스 분리

각 도메인의 서비스는 쓰기(`CommandService`)와 읽기(`QueryService`)로 분리해  
책임을 명확히 하고, 이후 읽기 최적화(CQRS 확장) 가능성을 열어 둡니다.

### 공통 예외 처리

`BaseErrorCode` 인터페이스를 각 도메인의 `ErrorCode` enum이 구현합니다.  
`ApplicationException`은 `BaseErrorCode`를 받아 생성되고, `GlobalExceptionHandler`에서 일괄 처리합니다.  
도메인마다 예외 코드가 독립적으로 관리되어 오류 추적이 용이합니다.

---

## Order Domain — Aggregate 설계

Order 도메인은 DDD 애그리게이트 패턴을 가장 충실하게 구현한 영역입니다.

### 애그리게이트 구성

`Order`를 루트 엔티티로, `OrderItem`과 `Delivery`를 내부 엔티티로 묶습니다.  
외부에서 `OrderItem`이나 `Delivery`에 직접 접근하지 않고, 반드시 `Order`를 통해 조작합니다.

```
Order (Aggregate Root)
├── OrderItem (N) — 주문 상품, 가격 스냅샷, 상태 관리
└── Delivery (1) — 수신자 정보, 배송 상태, 취소 가능 여부 판단
```

`CascadeType.PERSIST`를 적용해 `Order` 저장 시 `OrderItem`, `Delivery`가 함께 저장되며,  
연관관계 편의 메서드(`addOrderItem`, `linkDelivery`)로 양방향 일관성을 보장합니다.

### 상태 전이 캡슐화

상태 변경은 엔티티 외부에서 직접 필드를 수정하는 것이 아니라,  
각 상태 전이 메서드(`markPaid`, `cancel`, `startShipping` 등) 내부에서 유효성을 검증한 뒤 수행합니다.  
불변식 위반 시 즉시 `ApplicationException`을 던져 잘못된 상태 전이를 원천 차단합니다.

```
Order Status Flow

PENDING ──→ PAID ──→ SHIPPED ──→ DELIVERED
        ↘ PAYMENT_FAILED
        ↘ CANCELED
        ↘ PARTIAL_CANCELED
```

`Order.startShipping()`, `Order.completeDelivery()`는 내부적으로 `Delivery`의 상태 전이를 위임합니다.  
배송 도메인 로직이 Order 루트를 통해서만 실행되도록 강제하는 것이 핵심입니다.

### 주문 생성 플로우

```
1. User 조회 → user.getMemberId()로 memberId 안전하게 확보
   (IDENTITY 전략으로 userId == memberId 보장 불가)

2. 상품 다건 조회 → Map<Long, Product> 변환
   (N+1 방지: 상품 ID를 IN 절로 한 번에 조회)

3. 재고 차감 선행
   (실패 시 Order / OrderItem / Delivery 객체 자체를 생성하지 않음)

4. Order 생성 → OrderItem 추가 (가격 스냅샷) → Delivery 연결

5. Order 저장 (Cascade로 OrderItem, Delivery 함께 저장)

6. Payment 생성
   (IDENTITY 전략으로 orderId는 DB insert 후 확정 → Order 저장 완료 후 생성)
```

**가격 스냅샷**: `OrderItem`은 생성 시점의 `product.getPrice()`를 저장합니다.  
이후 상품 가격이 변경되어도 주문 당시 금액이 보존됩니다.

**배송지 Fallback**: 요청에 배송지가 없으면 서비스 계층에서 회원 기본 주소를 사용합니다.  
DTO에서 `@Valid` + `@Nullable` 조합으로, 배송지가 `null`이면 내부 필드 검증을 건너뜁니다.

---

## Payment System

### 결제 승인 플로우

클라이언트 → 서버 → 토스페이먼츠 3자 구조로 결제를 처리합니다.

```
1. Payment 조회 (비관락 — 중복 승인 요청 차단)

2. 금액 검증 (위변조 방지)
   클라이언트가 amount를 조작해서 넘길 수 있으므로
   DB에 저장된 totalAmount와 반드시 비교

3. PENDING → IN_PROGRESS 상태 전이

4. 토스페이먼츠 /v1/payments/confirm 호출 (WebClient)
   ├── 성공 (일반결제): IN_PROGRESS → DONE, Order → PAID
   ├── 성공 (가상계좌): IN_PROGRESS → WAITING_FOR_DEPOSIT
   └── 실패: IN_PROGRESS → FAILED, Order → PAYMENT_FAILED
```

```
Payment Status Flow

PENDING ──→ IN_PROGRESS ──→ DONE ──→ CANCELED
                         ↘ FAILED        ↘ PARTIAL_CANCELED
PENDING ──→ WAITING_FOR_DEPOSIT ──→ DONE
        ↘ EXPIRED
```

### 가상계좌 처리

가상계좌 결제는 승인 요청과 실제 입금이 분리됩니다.

- 승인 시: `WAITING_FOR_DEPOSIT` 상태로 전이, 토스로부터 받은 `secret` 저장
- 입금 완료 시: 토스가 Webhook으로 서버에 알림 → `secret` 검증 후 `DONE` 전이, `Order → PAID`

### 웹훅 멱등성 처리

토스 웹훅은 네트워크 재전송으로 동일 이벤트가 여러 번 수신될 수 있습니다.  
각 상태 전이 전에 현재 상태를 확인하고, 이미 처리된 상태이면 조용히 무시합니다.

```java
case "DONE" -> {
    if (foundPayment.getStatus() == PaymentStatus.DONE) {
        log.info("이미 처리된 DONE 웹훅: {}", data.orderId());
        return; // 멱등성 처리
    }
    ...
}
```

**망취소 대응**: 서버가 토스 API를 호출했으나 응답을 받지 못해 `FAILED`로 기록되었더라도,  
실제로는 결제가 완료된 경우 토스가 `DONE` 웹훅을 재전송합니다.  
웹훅 핸들러에서 `FAILED` 상태에서도 `DONE`으로 전이할 수 있도록 처리합니다.

### 취소 처리

전액 취소와 부분 취소를 `cancelAmount` 파라미터의 유무로 분기합니다.  
부분 취소 시 `canceledAmount`를 누적 합산하며, 누적 취소액이 총액과 같아지면 자동으로 전체 취소 상태로 전이합니다.

---

## Concurrency Control

### 재고 차감 — 비관적 락 + 데드락 방지

다수의 사용자가 동시에 같은 상품을 주문할 수 있으므로 `PESSIMISTIC_WRITE` 락을 적용합니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select i from Inventory i where i.productId in :productIds order by i.productId")
List<Inventory> findByProductIdsWithLock(List<Long> productIds);
```

여러 상품을 동시에 처리할 때 트랜잭션마다 락 획득 순서가 다르면 데드락이 발생합니다.  
이를 방지하기 위해 **항상 `productId` 오름차순으로 정렬된 상태**로 락을 획득합니다.  
모든 트랜잭션이 동일한 순서로 락을 획득하면 순환 대기가 발생하지 않습니다.

주문 취소 시 재고 복구에도 동일한 정렬 전략을 적용합니다.

### 결제 승인 — 비관적 락

결제 승인 요청이 중복으로 들어왔을 때 동시에 처리되면 이중 결제가 발생할 수 있습니다.  
`Payment` 조회 시 `PESSIMISTIC_WRITE` 락을 적용해, 하나의 트랜잭션만 상태 전이를 수행하도록 강제합니다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
@Query("select p from Payment p where p.orderNumber = :orderNumber")
Optional<Payment> findByOrderNumberForUpdate(String orderNumber);
```

### 관리자 재고 조정 — 락 미적용

관리자 수동 조정은 단일 요청 성격이 강해 동시 접근 가능성이 낮습니다.  
동시성 이슈가 발생하더라도 관리자가 직접 확인 후 재조정 가능한 운영 영역으로 판단하여 락을 적용하지 않았습니다.

---

## In Progress / TODO

- [ ] 주문 조회
- [ ] 주문 전체 / 부분 취소 API 연결
- [ ] 상품 검색 고도화 (QueryDSL 동적 쿼리)
- [ ] 테스트 코드 작성

---

## Getting Started

### 1. 환경변수 설정

프로젝트 루트에 `.env` 파일을 생성하고 아래 항목을 채웁니다.

```dotenv
# MySQL
MYSQL_ROOT_PASSWORD=
MYSQL_DATABASE=
MYSQL_USER=
MYSQL_PASSWORD=

# Application DB 연결
DB_URL=jdbc:mysql://localhost:3307/{MYSQL_DATABASE}?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
DB_USER=           # MYSQL_USER와 동일
DB_PASSWORD=       # MYSQL_PASSWORD와 동일

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# JWT
JWT_SECRET=        # 256bit 이상 랜덤 문자열 권장

# 토스페이먼츠
TOSS_SECRET_KEY=   # 토스페이먼츠 개발자 콘솔 > 시크릿 키
```

### 2. 인프라 실행 (Docker Compose)

```bash
docker-compose up -d
```

MySQL (3307 포트), Redis (6379 포트) 컨테이너가 실행됩니다.

### 3. 애플리케이션 실행

```bash
./gradlew bootRun
```
