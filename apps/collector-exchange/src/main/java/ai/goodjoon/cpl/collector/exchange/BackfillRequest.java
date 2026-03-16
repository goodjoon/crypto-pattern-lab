package ai.goodjoon.cpl.collector.exchange;

import java.time.Instant;

public record BackfillRequest(
    Instant from,
    Instant to
) {
}
