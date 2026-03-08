package kr.springware.profiler.core.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

public record ProfileEvent(
        String id,
        Instant timestamp,
        IssueCategory category,
        IssueSeverity severity,
        String description,
        String endpoint,
        String httpMethod,
        long elapsedMs,
        int httpStatus,
        Map<String, Object> metrics
) {
    public ProfileEvent(IssueCategory category, IssueSeverity severity, String description,
                        String endpoint, String httpMethod, long elapsedMs, int httpStatus,
                        Map<String, Object> metrics) {
        this(UUID.randomUUID().toString(), Instant.now(), category, severity, description,
                endpoint, httpMethod, elapsedMs, httpStatus, metrics);
    }

    public static ProfileEvent systemEvent(IssueCategory category, IssueSeverity severity,
                                           String description, Map<String, Object> metrics) {
        return new ProfileEvent(category, severity, description, "SYSTEM", "-", 0, 0, metrics);
    }
}
