package kr.springware.profiler.core.monitor;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

@Component
public class MemoryMonitor {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();

    public MemoryUsage getHeapUsage() {
        return memoryMXBean.getHeapMemoryUsage();
    }

    public long getHeapUsedMb() {
        return getHeapUsage().getUsed() / (1024 * 1024);
    }

    public long getHeapMaxMb() {
        long max = getHeapUsage().getMax();
        return max > 0 ? max / (1024 * 1024) : getHeapUsage().getCommitted() / (1024 * 1024);
    }

    public double getHeapUsagePercent() {
        MemoryUsage usage = getHeapUsage();
        long max = usage.getMax() > 0 ? usage.getMax() : usage.getCommitted();
        return (double) usage.getUsed() / max * 100;
    }
}
