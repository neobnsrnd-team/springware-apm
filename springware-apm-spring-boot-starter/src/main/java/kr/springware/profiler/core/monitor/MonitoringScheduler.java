package kr.springware.profiler.core.monitor;

import kr.springware.profiler.core.config.ProfilerConfig;
import kr.springware.profiler.core.detector.ThresholdDetector;
import org.springframework.scheduling.annotation.Scheduled;

public class MonitoringScheduler {

    private final ThresholdDetector detector;
    private final ProfilerConfig config;

    public MonitoringScheduler(ThresholdDetector detector, ProfilerConfig config) {
        this.detector = detector;
        this.config = config;
    }

    @Scheduled(fixedDelayString = "${profiler.monitoring-interval-ms:5000}")
    public void monitorSystem() {
        if (!config.isMonitoringEnabled()) {
            return;
        }
        detector.evaluateSystemMemory();
    }
}
