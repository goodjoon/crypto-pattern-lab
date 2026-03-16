package ai.goodjoon.cpl.collector.exchange;

import java.time.Instant;

public record Checkpoint(
    String collectorId,
    String cursorType,
    String cursorValue,
    Instant updatedAt
) {
}
