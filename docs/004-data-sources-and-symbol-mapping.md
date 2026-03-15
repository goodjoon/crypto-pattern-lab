# Crypto Pattern Lab — Data Sources and Symbol Mapping v0.1

## 1. 목적

이 문서는 Crypto Pattern Lab의 초기 데이터 소스와 심볼/마켓 정규화 전략을 정의한다.

핵심 목표는 다음과 같다.

- 거래소별 API와 데이터 특성을 비교한다.
- 국내 KRW 마켓과 해외 USDT 마켓을 공통 모델로 다룰 수 있게 한다.
- 온체인 자산과 거래소 심볼을 연결할 수 있는 기준을 만든다.
- 이후 패턴 분석/백테스트/리포트에서 재사용 가능한 표준 매핑 체계를 만든다.

## 2. 초기 데이터 소스 범위

### 2.1 국내 거래소

#### Upbit
초기 활용 목적:
- KRW 마켓 가격/체결 데이터 수집
- 국내 거래소 반응 속도 및 프리미엄/디스카운트 분석
- 글로벌 거래소와의 가격 전파 차이 분석

관찰 포인트:
- KRW 기준 가격 움직임
- 거래량 집중 시간대
- 특정 알트의 국내 선행/후행 반응

#### Coinone
초기 활용 목적:
- 국내 거래소 비교군 확보
- Upbit와 Coinone 간 체결 패턴/가격 차이 비교
- 국내 시장 내부의 미시구조 차이 확인

관찰 포인트:
- 동일 자산의 거래소별 체결 편향
- 국내 거래소 내 유동성 차이
- 특정 코인의 상장/거래 집중 특성

### 2.2 해외 거래소

#### Binance
초기 활용 목적:
- 글로벌 기준 가격/유동성 벤치마크
- 현물/선물/펀딩/오픈이자 등 풍부한 데이터 소스 활용
- 글로벌 가격 발견이 주로 어디서 시작되는지 확인

관찰 포인트:
- USDT 마켓 기준 가격 선도성
- 현물 vs 선물 관계
- 펀딩/오픈이자와 단기 방향성 상관

#### Bybit
초기 활용 목적:
- Binance 대비 가격/체결/파생 반응 비교
- 글로벌 거래소 간 괴리/지연 연구
- 특정 구간에서 Binance 주도인지 Bybit 주도인지 확인

관찰 포인트:
- 동일 자산의 짧은 시차 반응
- 파생 중심 지표 변화
- 특정 급등락 시점의 거래소별 반응 차이

## 3. 온체인 데이터 소스 범위

### 3.1 Ethereum mainnet

초기 활용 목적:
- ERC-20 transfer 추적
- 거래소 관련 지갑 흐름 추적
- 고래 이동 및 대규모 자금 이동 탐지
- 메인넷 기준 브리지/정산 흐름 파악

### 3.2 Ethereum L2

초기 후보:
- Arbitrum
- Optimism
- Base

초기 활용 목적:
- 브리지 유입/유출 추적
- DEX 거래/토큰 이동 패턴 탐색
- 특정 체인에서 먼저 발생하는 유동성 변화 감지

## 4. 초기에 수집할 데이터 타입

### 4.1 거래소 데이터

우선순위 A:
- ticker
- trade stream
- order book snapshot/depth

우선순위 B:
- funding rate
- open interest
- mark/index price

우선순위 C:
- listing/delisting 공지
- 거래소 메타데이터

### 4.2 온체인 데이터

우선순위 A:
- ERC-20 transfer
- native token transfer
- bridge inflow/outflow

우선순위 B:
- DEX swap
- liquidity add/remove
- 주요 컨트랙트 interaction count

우선순위 C:
- 거버넌스/언락/프로토콜 이벤트
- 라벨링된 주소군 변화

## 5. 심볼 정규화가 필요한 이유

거래소마다 동일 자산을 다르게 표현할 수 있다.

예시:
- KRW-BTC
- BTC_KRW
- BTCUSDT
- BTC/USDT

또한 온체인에서는 자산을 ticker가 아니라 contract address로 식별한다.

따라서 연구 시스템에서는 반드시 아래를 분리해야 한다.

- **asset**: 자산 자체 (예: BTC, ETH, USDC)
- **instrument/market**: 거래쌍 (예: ETH/KRW, ETH/USDT)
- **venue**: 거래소/체인/프로토콜
- **onchain token id**: 체인 + contract address

## 6. 표준 식별자 제안

### 6.1 Asset ID

공통 자산 식별자.

예시:
- `BTC`
- `ETH`
- `USDT`
- `USDC`
- `ARB`

주의:
- ticker 충돌 가능성이 있으므로 장기적으로는 내부 asset registry가 필요함
- 같은 ticker라도 체인/발행처가 다르면 별도 식별이 필요할 수 있음

### 6.2 Market ID

거래쌍 식별자.

표준 포맷 제안:
- `{BASE}-{QUOTE}`

예시:
- `BTC-KRW`
- `ETH-KRW`
- `BTC-USDT`
- `ETH-USDT`

### 6.3 Venue ID

거래소/체인 식별자.

예시:
- `UPBIT`
- `COINONE`
- `BINANCE`
- `BYBIT`
- `ETHEREUM`
- `ARBITRUM`
- `OPTIMISM`
- `BASE`

### 6.4 Instrument Key

분석용 고유 키.

표준 포맷 제안:
- `{VENUE}:{BASE}-{QUOTE}`

예시:
- `UPBIT:BTC-KRW`
- `COINONE:ETH-KRW`
- `BINANCE:BTC-USDT`
- `BYBIT:ETH-USDT`

### 6.5 On-chain Token Key

표준 포맷 제안:
- `{CHAIN}:{CONTRACT_ADDRESS}`

예시:
- `ETHEREUM:0xa0b86991c6218b36c1d19d4a2e9eb0ce3606eb48`
- `ARBITRUM:0xaf88d065e77c8cc2239327c5edb3a432268e5831`

## 7. 심볼 매핑 규칙 초안

### 7.1 거래소 원본 → 표준 마켓 변환

예시 매핑:
- Upbit `KRW-BTC` → `UPBIT:BTC-KRW`
- Coinone `BTC` + quote `KRW` → `COINONE:BTC-KRW`
- Binance `BTCUSDT` → `BINANCE:BTC-USDT`
- Bybit `ETHUSDT` → `BYBIT:ETH-USDT`

핵심 원칙:
- 원본 심볼은 항상 별도 저장
- 정규화된 market id도 함께 저장
- base/quote 분리 저장
- venue 포함한 instrument key를 생성

### 7.2 온체인 자산 연결

예시:
- Binance `ETH-USDT`
- Upbit `ETH-KRW`
- On-chain `ETHEREUM:native-ETH`
- On-chain `ETHEREUM:{USDT_CONTRACT}`

즉, 분석 시스템은 아래 관계를 추적해야 한다.
- `asset ETH` 는 여러 venue에서 거래된다.
- `asset USDT` 는 체인별 contract가 다를 수 있다.
- 특정 거래쌍은 거래소마다 quote가 다르다.

## 8. 초기 자산 universe 제안

초기에는 너무 넓게 잡지 않는다.

### Core assets
- BTC
- ETH

### Ethereum ecosystem / L2 related candidates
- ARB
- OP
- STRK 또는 기타 주요 L2 관련 자산 (후속 검토)
- BASE 관련 직접 토큰은 없으므로 ecosystem proxy 자산 검토

### Stablecoins
- USDT
- USDC

### 국내 상장성과 글로벌 유동성을 함께 보기 좋은 자산
- ETH
- XRP
- SOL
- 주요 알트 일부 (추후 거래소 공통 상장 기준으로 선정)

## 9. 연구 관점의 매핑 이슈

### 9.1 KRW vs USDT 문제

동일 자산이라도 quote currency가 다르므로 직접 비교 시 주의가 필요하다.

필요한 것:
- KRW/USD 환산 기준
- 스테이블코인 디페그 가능성 고려
- 시간 동기화
- 수수료/환전/자본 이동 제약 분리 고려

### 9.2 스테이블코인 멀티체인 문제

USDT/USDC는 여러 체인에 존재한다.

따라서 아래를 분리해야 한다.
- 자산 관점의 `USDT`
- 체인별 실제 토큰 `ETHEREUM:USDT_contract`, `ARBITRUM:USDT_contract` 등

### 9.3 거래소 심볼 충돌 문제

일부 ticker는 거래소마다 의미가 다르거나 구버전/신버전 자산이 섞일 수 있다.

따라서:
- ticker만 믿지 말고 메타데이터 확인
- 가능하면 contract address 및 공식 asset id 연동
- 수동 검증 리스트 유지

## 10. 저장 스키마 초안

### 10.1 instruments
- instrument_key
- venue
- raw_symbol
- normalized_market
- base_asset
- quote_asset
- active
- metadata_json

### 10.2 assets
- asset_id
- display_name
- category
- canonical_chain (optional)
- metadata_json

### 10.3 onchain_tokens
- token_key
- chain
- contract_address
- asset_id
- decimals
- symbol
- metadata_json

### 10.4 venue_symbol_mapping_audit
- venue
- raw_symbol
- mapped_instrument_key
- mapping_version
- created_at
- verified

## 11. 초기 구현 순서 추천

### Step 1
- Binance / Bybit 심볼 매핑부터 구현
- ETH, BTC, USDT 페어 우선

### Step 2
- Upbit KRW 마켓 연결
- 국내/해외 공통 자산 교집합 선정

### Step 3
- Coinone 추가
- 국내 거래소 내부 비교 추가

### Step 4
- Ethereum mainnet token registry 추가
- 거래소 자산과 온체인 자산 연결

### Step 5
- L2 token registry 및 bridge flow 확장

## 12. 다음 액션

1. 공통 symbol mapping JSON 또는 테이블 포맷 정의
2. 초기 자산 10~20개 shortlist 작성
3. 거래소 API capabilities 문서 보강
4. on-chain token registry 전략 문서화
5. collector 구현용 인터페이스 초안 작성
