# Crypto Pattern Lab — Common Schema v0.1

## 1. 목적

이 문서는 거래소 데이터와 온체인 데이터를 공통적으로 다루기 위한 초기 스키마 초안을 정의한다.

목표는 다음과 같다.
- 서로 다른 source를 같은 분석 파이프라인에서 사용할 수 있게 한다.
- raw event와 normalized event를 분리한다.
- market / asset / token / venue 개념을 명확히 분리한다.
- 향후 백테스트/리포트/신호 엔진에서 재사용 가능한 표준 구조를 만든다.

## 2. 스키마 레이어

### 2.1 Raw Schema
원본 이벤트를 source-specific 형태로 저장한다.

필수 필드:
- source_type (exchange/onchain)
- venue
- event_type
- raw_payload
- event_time
- ingest_time
- source_cursor
- dedup_key

### 2.2 Normalized Schema
분석용 공통 모델로 변환한 이벤트.

필수 필드:
- event_id
- event_type
- venue
- instrument_key 또는 token_key
- event_time
- ingest_time
- attributes

## 3. 핵심 엔터티

### 3.1 Asset
자산 자체를 의미한다.

예:
- BTC
- ETH
- USDT
- USDC
- ARB

필드 예시:
- asset_id
- symbol
- display_name
- asset_category
- status
- metadata_json

### 3.2 Instrument
거래 가능한 market pair.

예:
- `UPBIT:ETH-KRW`
- `BINANCE:ETH-USDT`

필드 예시:
- instrument_key
- venue
- raw_symbol
- normalized_market
- base_asset
- quote_asset
- market_type
- status
- metadata_json

### 3.3 Onchain Token
체인 위 실제 토큰 식별자.

예:
- `ETHEREUM:0xa0b8...`
- `ARBITRUM:0xaf88...`

필드 예시:
- token_key
- chain
- contract_address
- asset_id
- decimals
- symbol
- metadata_json

## 4. Raw Event 모델

```json
{
  "sourceType": "exchange",
  "venue": "BINANCE",
  "eventType": "trade_raw",
  "eventTime": "2026-03-16T00:00:00Z",
  "ingestTime": "2026-03-16T00:00:01Z",
  "sourceCursor": "tradeId:123456789",
  "dedupKey": "BINANCE:BTCUSDT:123456789",
  "rawPayload": {}
}
```

원칙:
- payload는 최대한 원본 보존
- raw 단계에서는 venue별 차이를 억지로 숨기지 않음

## 5. Normalized Event 종류

초기 이벤트 타입은 아래 정도로 시작한다.

### 5.1 Trade Event
필드 예시:
- event_id
- venue
- instrument_key
- event_time
- price
- quantity
- side_aggressor
- trade_id

### 5.2 Orderbook Snapshot Event
필드 예시:
- event_id
- venue
- instrument_key
- event_time
- bids
- asks
- sequence

### 5.3 Funding Event
필드 예시:
- instrument_key
- funding_rate
- funding_time

### 5.4 Open Interest Event
필드 예시:
- instrument_key
- open_interest
- event_time

### 5.5 Token Transfer Event
필드 예시:
- token_key
- asset_id
- chain
- tx_hash
- block_number
- from_address
- to_address
- amount_raw
- amount_normalized
- event_time

### 5.6 Dex Swap Event
필드 예시:
- chain
- pool_address
- token_in_key
- token_out_key
- amount_in
- amount_out
- tx_hash
- trader_address
- event_time

### 5.7 Bridge Flow Event
필드 예시:
- source_chain
- destination_chain
- token_key
- amount
- bridge_protocol
- tx_hash
- event_time

## 6. 시간 필드 원칙

시간은 최소 두 개를 분리한다.

- `event_time`: 실제 이벤트가 발생한 시각
- `ingest_time`: 시스템이 그 이벤트를 수집한 시각

필요 시 추가:
- `processed_time`

이 구분이 중요한 이유:
- 지연 측정 가능
- 소스별 반응 속도 비교 가능
- 이벤트 전파 시간 분석 가능

## 7. 키 설계 원칙

### event_id
- normalized event의 내부 고유 ID
- ULID/UUID 등 사용 가능

### dedup_key
- raw event 중복 제거용
- venue/event type/source cursor 기반 추천

### instrument_key
- `{VENUE}:{BASE}-{QUOTE}`

### token_key
- `{CHAIN}:{CONTRACT_ADDRESS}`

## 8. 저장소 관점 권장 테이블

### raw_events
- raw_event_id
- source_type
- venue
- event_type
- event_time
- ingest_time
- source_cursor
- dedup_key
- raw_payload_json

### trade_events
- event_id
- venue
- instrument_key
- event_time
- ingest_time
- price
- quantity
- side_aggressor
- trade_id

### orderbook_events
- event_id
- venue
- instrument_key
- event_time
- sequence
- bids_json
- asks_json

### token_transfer_events
- event_id
- chain
- token_key
- asset_id
- tx_hash
- block_number
- from_address
- to_address
- amount_raw
- amount_normalized
- event_time

## 9. 분석용 파생 테이블 예시

초기부터 materialized/derived view를 고려한다.

예:
- `market_minute_bars`
- `exchange_spread_snapshots`
- `whale_transfer_alerts`
- `bridge_flow_aggregates`
- `cross_venue_price_gap`

## 10. 스키마 진화 원칙

- raw schema는 최대한 안정적으로 유지
- normalized schema는 버전 필드 도입 고려
- 이벤트 타입별 optional field 허용
- 파생 분석 스키마는 자유롭게 진화

## 11. 초기 구현 최소셋

먼저 구현할 최소 normalized event:
- trade_event
- orderbook_snapshot_event
- token_transfer_event

이 세 가지면:
- 거래소 가격/체결 패턴 연구
- 기본 미시구조 분석
- 거래소/온체인 연결 연구
까지 시작할 수 있다.

## 12. 다음 액션

1. schema를 문서에서 코드로 옮기기
2. Java record / class 초안 생성
3. DB migration 초안 작성
4. sample payload 기반 매핑 테스트 작성
