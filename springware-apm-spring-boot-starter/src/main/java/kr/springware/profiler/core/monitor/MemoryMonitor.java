package kr.springware.profiler.core.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import com.sun.management.ThreadMXBean;

public class MemoryMonitor {

    private final MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    private final ThreadMXBean threadMXBean;

    public MemoryMonitor() {
        this.threadMXBean = ManagementFactory.getPlatformMXBean(ThreadMXBean.class);
        if (threadMXBean != null && threadMXBean.isThreadAllocatedMemorySupported()) {
            threadMXBean.setThreadAllocatedMemoryEnabled(true);
        }
    }

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

    public long getCurrentThreadAllocatedBytes() {
        if (threadMXBean != null && threadMXBean.isThreadAllocatedMemoryEnabled()) {
            return threadMXBean.getCurrentThreadAllocatedBytes();
        }
        return 0;
    }
}
