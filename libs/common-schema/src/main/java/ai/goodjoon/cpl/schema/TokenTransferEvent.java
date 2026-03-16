package ai.goodjoon.cpl.schema;

import java.math.BigDecimal;
import java.time.Instant;

public record TokenTransferEvent(
    String eventId,
    String chain,
    String tokenKey,
    String assetId,
    String txHash,
    long blockNumber,
    String fromAddress,
    String toAddress,
    BigDecimal amountRaw,
    BigDecimal amountNormalized,
    Instant eventTime,
    Instant ingestTime
) {
}
