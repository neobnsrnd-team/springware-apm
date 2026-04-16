package kr.springware.profiler.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "profiler")
public class ProfilerConfig {

    private Threshold threshold = new Threshold();
    private long monitoringIntervalMs = 5000;
    private int maxEvents = 1000;
    private volatile String collectMode = "all";
    private boolean dashboardTriggerEnabled = false;
    private volatile boolean monitoringEnabled = true;

    public String getCollectMode() {
        return collectMode;
    }

    public void setCollectMode(String collectMode) {
        this.collectMode = collectMode;
    }

    public boolean isIssuesOnly() {
        return "issues-only".equals(collectMode);
    }

    public Threshold getThreshold() {
        return threshold;
    }

    public void setThreshold(Threshold threshold) {
        this.threshold = threshold;
    }

    public long getMonitoringIntervalMs() {
        return monitoringIntervalMs;
    }

    public void setMonitoringIntervalMs(long monitoringIntervalMs) {
        this.monitoringIntervalMs = monitoringIntervalMs;
    }

    public int getMaxEvents() {
        return maxEvents;
    }

    public void setMaxEvents(int maxEvents) {
        this.maxEvents = maxEvents;
    }

    public boolean isDashboardTriggerEnabled() {
        return dashboardTriggerEnabled;
    }

    public void setDashboardTriggerEnabled(boolean dashboardTriggerEnabled) {
        this.dashboardTriggerEnabled = dashboardTriggerEnabled;
    }

    public boolean isMonitoringEnabled() {
        return monitoringEnabled;
    }

    public void setMonitoringEnabled(boolean monitoringEnabled) {
        this.monitoringEnabled = monitoringEnabled;
    }

    public static class Threshold {
        private long responseTimeMs = 3000;
        private double cpuPercent = 80;
        private double memoryPercent = 85;
        private long memorySpikeMb = 50;

        public long getResponseTimeMs() {
            return responseTimeMs;
        }

        public void setResponseTimeMs(long responseTimeMs) {
            this.responseTimeMs = responseTimeMs;
        }

        public double getCpuPercent() {
            return cpuPercent;
        }

        public void setCpuPercent(double cpuPercent) {
            this.cpuPercent = cpuPercent;
        }

        public double getMemoryPercent() {
            return memoryPercent;
        }

        public void setMemoryPercent(double memoryPercent) {
            this.memoryPercent = memoryPercent;
        }

        public long getMemorySpikeMb() {
            return memorySpikeMb;
        }

        public void setMemorySpikeMb(long memorySpikeMb) {
            this.memorySpikeMb = memorySpikeMb;
        }
    }
}
