package kr.springware.profiler.core.detector;

import kr.springware.profiler.core.config.ProfilerConfig;
import kr.springware.profiler.core.model.IssueCategory;
import kr.springware.profiler.core.model.IssueSeverity;
import kr.springware.profiler.core.model.ProfileEvent;
import kr.springware.profiler.core.monitor.MemoryMonitor;
import kr.springware.profiler.core.store.ProfileEventStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ThresholdDetector {

    private final ProfilerConfig config;
    private final ProfileEventStore store;
    private final MemoryMonitor memoryMonitor;

    public ThresholdDetector(ProfilerConfig config, ProfileEventStore store, MemoryMonitor memoryMonitor) {
        this.config = config;
        this.store = store;
        this.memoryMonitor = memoryMonitor;
    }

    public void evaluateRequest(String endpoint, String httpMethod, long elapsedMs,
                                long cpuTimeNanos, long memoryDeltaBytes, int httpStatus) {
        var threshold = config.getThreshold();
        List<ProfileEvent> detected = new ArrayList<>();

        // Slow execution detection
        if (elapsedMs > threshold.getResponseTimeMs()) {
            IssueSeverity severity = elapsedMs > threshold.getResponseTimeMs() * 3
                    ? IssueSeverity.CRITICAL : IssueSeverity.WARNING;
            detected.add(new ProfileEvent(
                    IssueCategory.SLOW_EXECUTION, severity,
                    String.format("Slow response: %dms (threshold: %dms)", elapsedMs, threshold.getResponseTimeMs()),
                    endpoint, httpMethod, elapsedMs, httpStatus,
                    Map.of("elapsedMs", elapsedMs, "thresholdMs", threshold.getResponseTimeMs())
            ));
        }

        // CPU detection (per-thread CPU time)
        long cpuTimeMs = cpuTimeNanos / 1_000_000;
        double cpuRatio = elapsedMs > 0 ? (double) cpuTimeMs / elapsedMs * 100 : 0;
        if (cpuRatio > threshold.getCpuPercent()) {
            IssueSeverity severity = cpuRatio > threshold.getCpuPercent() * 3
                    ? IssueSeverity.CRITICAL : IssueSeverity.WARNING;
            detected.add(new ProfileEvent(
                    IssueCategory.CPU, severity,
                    String.format("High CPU usage: %.1f%% (threshold: %.0f%%)", cpuRatio, threshold.getCpuPercent()),
                    endpoint, httpMethod, elapsedMs, httpStatus,
                    Map.of("cpuRatio", cpuRatio, "cpuTimeMs", cpuTimeMs, "elapsedMs", elapsedMs)
            ));
        }

        // Memory spike detection
        long memoryDeltaMb = memoryDeltaBytes / (1024 * 1024);
        if (memoryDeltaMb > threshold.getMemorySpikeMb()) {
            IssueSeverity severity = memoryDeltaMb > threshold.getMemorySpikeMb() * 3
                    ? IssueSeverity.CRITICAL : IssueSeverity.WARNING;
            detected.add(new ProfileEvent(
                    IssueCategory.MEMORY, severity,
                    String.format("Memory spike: %dMB (threshold: %dMB)", memoryDeltaMb, threshold.getMemorySpikeMb()),
                    endpoint, httpMethod, elapsedMs, httpStatus,
                    Map.of("memoryDeltaMb", memoryDeltaMb, "thresholdMb", threshold.getMemorySpikeMb())
            ));
        }

        // If nothing alarming, still record INFO for XLog (unless issues-only mode)
        if (detected.isEmpty()) {
            if (!config.isIssuesOnly()) {
                detected.add(new ProfileEvent(
                        IssueCategory.SLOW_EXECUTION, IssueSeverity.INFO,
                        String.format("Request completed in %dms", elapsedMs),
                        endpoint, httpMethod, elapsedMs, httpStatus,
                        Map.of("elapsedMs", elapsedMs, "cpuTimeMs", cpuTimeMs, "memoryDeltaMb", memoryDeltaMb)
                ));
            }
        }

        detected.forEach(store::add);
    }

    public void evaluateSystemMemory() {
        var threshold = config.getThreshold();
        double heapPercent = memoryMonitor.getHeapUsagePercent();

        if (heapPercent > threshold.getMemoryPercent()) {
            IssueSeverity severity = heapPercent > threshold.getMemoryPercent() * 1.1
                    ? IssueSeverity.CRITICAL : IssueSeverity.WARNING;
            store.add(ProfileEvent.systemEvent(
                    IssueCategory.MEMORY, severity,
                    String.format("High heap usage: %.1f%% (threshold: %.0f%%)", heapPercent, threshold.getMemoryPercent()),
                    Map.of("heapPercent", heapPercent, "heapUsedMb", memoryMonitor.getHeapUsedMb(),
                            "heapMaxMb", memoryMonitor.getHeapMaxMb())
            ));
        }
    }
}
