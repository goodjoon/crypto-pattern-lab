# Crypto Pattern Lab — Next Steps Roadmap v0.1

## 1. 지금까지 만든 문서

- `000-project-charter.md`
- `001-system-design-v0.md`
- `002-mvp-scope.md`
- `003-ethereum-ecosystem-brief.md`
- `004-data-sources-and-symbol-mapping.md`

## 2. 바로 다음 실행 항목

### Track A — 도메인/리서치 정리
1. 초기 자산 shortlist 작성
2. 최신 Ethereum/L2 중요 플레이어 정리
3. 거래소/온체인 이벤트 우선순위 정리

### Track B — 아키텍처 구체화
1. collector interface 설계
2. common schema 설계
3. raw/normalized 저장 구조 설계
4. 개발용 로컬 실행 구조 설계

### Track C — 프로젝트 골격 생성
1. `apps/api`
2. `apps/collector-exchange`
3. `apps/collector-onchain`
4. `apps/research-worker`
5. `libs/common-schema`
6. `infra/docker`
7. Gradle wrapper 추가

## 3. 추천 진행 순서

### Phase 1
- 문서 기반 정렬 완료
- 자산/거래소/체인 범위 확정

### Phase 2
- repo 디렉토리 골격 생성
- 공통 스키마 초안 작성
- collector interface 작성

### Phase 3
- Binance/Bybit collector prototype
- 기본 market data 적재

### Phase 4
- Upbit 연결
- 국내/해외 비교 리포트 초안

### Phase 5
- Ethereum mainnet transfer collector
- 거래소 주소 라벨링 기초 작업

## 4. 미니의 추천 즉시 작업

다음으로는 문서만 더 쓰기보다, 이제 **프로젝트 코드/디렉토리 골격을 생성**하는 게 좋다.

권장 산출물:
- 루트 폴더 구조
- README 확장
- collector interface 초안
- common schema 문서 또는 코드 스텁
- docker-compose 초안
- 아키텍처 원칙 문서
- 개발 원칙 문서
