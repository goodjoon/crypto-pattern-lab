package ai.goodjoon.cpl.collector.exchange;

public record HealthStatus(
    boolean healthy,
    String message
) {
}
