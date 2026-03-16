package ai.goodjoon.cpl.schema;

import java.util.Map;

public record Asset(
    String assetId,
    String symbol,
    String displayName,
    String assetCategory,
    String status,
    Map<String, Object> metadata
) {
}
