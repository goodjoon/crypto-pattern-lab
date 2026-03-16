# Crypto Pattern Lab — Collector Interface v0.1

## 1. 목적

이 문서는 거래소 수집기와 온체인 수집기가 공통적으로 따라야 할 인터페이스 초안을 정의한다.

목표는 다음과 같다.
- 수집기 구현 방식을 통일한다.
- 거래소/체인별 차이를 adapter 레이어로 격리한다.
- raw 이벤트 수집과 normalized 이벤트 생성의 책임을 분리한다.
- 백필(backfill)과 실시간(streaming) 수집을 같은 추상 모델로 다룬다.

## 2. 설계 원칙

- collector는 source-specific 하되 lifecycle은 공통화한다.
- raw 저장과 normalized 변환은 분리한다.
- 장애/재시도/체크포인트는 인터페이스 수준에서 고려한다.
- 실시간 수집과 배치 백필을 같은 이벤트 모델로 연결한다.
- 처음부터 완벽한 범용성보다, Binance/Bybit/Upbit/Ethereum에 맞는 실용적 추상화를 택한다.

## 3. collector 유형

### 3.1 Exchange Market Data Collector
수집 대상:
- ticker
- trades
- orderbook depth/snapshot
- funding/open interest 등 파생 지표

### 3.2 Exchange Metadata Collector
수집 대상:
- 상장 자산 목록
- 심볼 메타데이터
- market status
- 상장/폐지 이벤트

### 3.3 On-chain Event Collector
수집 대상:
- native transfer
- ERC-20 transfer
- DEX swap
- bridge inflow/outflow
- 특정 컨트랙트 이벤트

## 4. 공통 lifecycle 제안

모든 collector는 아래 lifecycle을 가진다.

1. `discover()`
   - 수집 가능한 market / contract / stream 확인
2. `bootstrap()`
   - 초기 메타데이터 적재
3. `backfill()`
   - 과거 데이터 수집
4. `stream()`
   - 실시간 데이터 수집
5. `checkpoint()`
   - 마지막 처리 위치 저장
6. `health()`
   - 상태 점검
7. `shutdown()`
   - 정상 종료

## 5. 인터페이스 초안

언어는 Java 중심을 가정하지만, 개념은 공통이다.

```java
public interface Collector<TConfig> {
    String collectorId();
    CollectorType type();
    TConfig config();

    void discover() throws Exception;
    void bootstrap() throws Exception;
    void backfill(BackfillRequest request) throws Exception;
    void stream() throws Exception;
    HealthStatus health();
    Checkpoint checkpoint();
    void shutdown() throws Exception;
}
```

보조 타입 예시:

```java
public enum CollectorType {
    EXCHANGE_MARKET_DATA,
    EXCHANGE_METADATA,
    ONCHAIN_EVENT
}
```

## 6. Source Adapter 분리

실제 거래소/체인 특화 로직은 adapter로 분리한다.

예시:

```java
public interface ExchangeAdapter {
    String venue();
    List<MarketDescriptor> discoverMarkets();
    void backfillTrades(MarketDescriptor market, TimeRange range, RawEventSink sink);
    void subscribeTrades(List<MarketDescriptor> markets, RawEventSink sink);
    void subscribeOrderBook(List<MarketDescriptor> markets, RawEventSink sink);
}
```

```java
public interface OnchainAdapter {
    String chain();
    List<ContractDescriptor> discoverContracts();
    void backfillLogs(ContractDescriptor contract, BlockRange range, RawEventSink sink);
    void subscribeLogs(List<ContractDescriptor> contracts, RawEventSink sink);
}
```

핵심은:
- collector는 orchestration 담당
- adapter는 source 연결 담당
- sink는 raw event 저장 담당

## 7. Sink 인터페이스

수집기는 raw event를 직접 DB에 박아도 되지만, 초기엔 sink 추상화를 두는 게 낫다.

```java
public interface RawEventSink {
    void write(RawEvent event);
    void writeBatch(List<RawEvent> events);
}
```

장점:
- 파일/DB/queue 대체 가능
- 테스트 쉬움
- batch write 최적화 가능

## 8. Checkpoint 모델

실시간/백필 수집에서 체크포인트는 필수다.

예시:
- 거래소 stream: last event time, last sequence id
- REST backfill: last fetched timestamp
- on-chain: last block number, last log index

예시 모델:

```java
public record Checkpoint(
    String collectorId,
    String cursorType,
    String cursorValue,
    Instant updatedAt
) {}
```

## 9. 오류 처리 전략

수집기 수준에서 최소한 아래를 고려한다.

- transient error 재시도
- rate limit 감지
- reconnect backoff
- 데이터 gap 감지
- 중복 이벤트 허용 후 downstream dedup 전략

원칙:
- 유실보다 중복이 낫다
- 정확한 once-only보다 recoverable-at-least-once를 우선한다

## 10. 초기 구현 우선순위

### Phase 1
- Binance / Bybit market data collector
- trade + ticker + basic depth

### Phase 2
- Upbit market data collector
- KRW market 지원

### Phase 3
- Ethereum transfer collector
- ERC-20 transfer 기반 이벤트 추출

### Phase 4
- bridge / DEX event collector

## 11. 권장 디렉토리 매핑

- `apps/collector-exchange` → 거래소 수집기 orchestration
- `apps/collector-onchain` → 온체인 수집기 orchestration
- `libs/common-schema` → raw/normalized event model
- `libs/connectors` → 거래소/체인 adapter 모음

## 12. 다음 액션

1. Java package 구조 정하기
2. collector interface 코드 스텁 생성
3. Binance adapter 초안 생성
4. Ethereum adapter 초안 생성
5. raw event sink를 file/db 둘 중 하나로 우선 구현
