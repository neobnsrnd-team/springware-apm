package kr.springware.profiler.core.monitor;

import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

@Component
public class CpuMonitor {

    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public CpuMonitor() {
        if (threadMXBean.isThreadCpuTimeSupported()) {
            threadMXBean.setThreadCpuTimeEnabled(true);
        }
    }

    public long getCurrentThreadCpuTimeNanos() {
        return threadMXBean.getCurrentThreadCpuTime();
    }

    public double getProcessCpuLoad() {
        var osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
            return sunBean.getProcessCpuLoad() * 100;
        }
        return -1;
    }

    public double getSystemCpuLoad() {
        var osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean sunBean) {
            return sunBean.getCpuLoad() * 100;
        }
        return -1;
    }
}
