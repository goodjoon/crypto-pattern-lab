package ai.goodjoon.cpl.collector.exchange;

public interface Collector<TConfig> {

    String collectorId();

    CollectorType type();

    TConfig config();

    void discover() throws Exception;

    void bootstrap() throws Exception;

    void backfill(BackfillRequest request) throws Exception;

    void stream() throws Exception;

    HealthStatus health();

    Checkpoint checkpoint();

    void shutdown() throws Exception;
}
