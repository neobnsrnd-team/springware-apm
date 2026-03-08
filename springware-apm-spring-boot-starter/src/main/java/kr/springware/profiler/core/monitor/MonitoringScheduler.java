package kr.springware.profiler.core.monitor;

import kr.springware.profiler.core.detector.ThresholdDetector;
import org.springframework.scheduling.annotation.Scheduled;

public class MonitoringScheduler {

    private final ThresholdDetector detector;

    public MonitoringScheduler(ThresholdDetector detector) {
        this.detector = detector;
    }

    @Scheduled(fixedDelayString = "${profiler.monitoring-interval-ms:5000}")
    public void monitorSystem() {
        detector.evaluateSystemMemory();
    }
}
