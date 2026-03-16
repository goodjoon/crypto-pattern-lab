package ai.goodjoon.cpl.connectors.binance;

import java.util.List;

public class BinanceExchangeAdapter {

    public String venue() {
        return "BINANCE";
    }

    public List<String> discoverMarkets() {
        // 추후 실제 바이낸스 시장 메타데이터 조회 로직을 넣는다.
        return List.of("BTC-USDT", "ETH-USDT");
    }
}
