package ai.goodjoon.cpl.schema;

import java.math.BigDecimal;
import java.time.Instant;

public record TradeEvent(
    String eventId,
    String venue,
    String instrumentKey,
    Instant eventTime,
    Instant ingestTime,
    BigDecimal price,
    BigDecimal quantity,
    String sideAggressor,
    String tradeId
) {
}
