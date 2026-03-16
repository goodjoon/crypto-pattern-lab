package ai.goodjoon.cpl.schema;

import java.util.Map;

public record Instrument(
    String instrumentKey,
    String venue,
    String rawSymbol,
    String normalizedMarket,
    String baseAsset,
    String quoteAsset,
    String marketType,
    String status,
    Map<String, Object> metadata
) {
}
