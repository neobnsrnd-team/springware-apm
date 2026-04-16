package kr.springware.profiler.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.springware.profiler.core.config.ProfilerConfig;
import kr.springware.profiler.core.detector.ThresholdDetector;
import kr.springware.profiler.core.monitor.ActiveThreadTracker;
import kr.springware.profiler.core.monitor.CpuMonitor;
import kr.springware.profiler.core.monitor.MemoryMonitor;
import kr.springware.profiler.core.store.ProfileEventStore;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfilingFilter extends OncePerRequestFilter {

    private final CpuMonitor cpuMonitor;
    private final MemoryMonitor memoryMonitor;
    private final ThresholdDetector detector;
    private final ActiveThreadTracker threadTracker;
    private final ProfileEventStore store;
    private final ProfilerConfig config;

    public ProfilingFilter(CpuMonitor cpuMonitor, MemoryMonitor memoryMonitor,
                           ThresholdDetector detector, ActiveThreadTracker threadTracker,
                           ProfileEventStore store, ProfilerConfig config) {
        this.cpuMonitor = cpuMonitor;
        this.memoryMonitor = memoryMonitor;
        this.detector = detector;
        this.threadTracker = threadTracker;
        this.store = store;
        this.config = config;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Use servletPath (context-path stripped) so this works under any deploy context
        String path = request.getServletPath();
        return path.startsWith("/api/profiler")
                || path.startsWith("/h2-console")
                || path.startsWith("/favicon");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        if (!config.isMonitoringEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        String endpoint = request.getMethod() + " " + request.getRequestURI();
        if (request.getQueryString() != null) {
            endpoint += "?" + request.getQueryString();
        }

        threadTracker.register(endpoint);
        store.recordRequest();
        long startTime = System.currentTimeMillis();
        long startCpuTime = cpuMonitor.getCurrentThreadCpuTimeNanos();
        long startAllocBytes = memoryMonitor.getCurrentThreadAllocatedBytes();

        try {
            filterChain.doFilter(request, response);
        } finally {
            threadTracker.unregister();
            long elapsedMs = System.currentTimeMillis() - startTime;
            long cpuTimeNanos = cpuMonitor.getCurrentThreadCpuTimeNanos() - startCpuTime;
            long allocDeltaBytes = memoryMonitor.getCurrentThreadAllocatedBytes() - startAllocBytes;

            detector.evaluateRequest(
                    endpoint,
                    request.getMethod(),
                    elapsedMs,
                    cpuTimeNanos,
                    allocDeltaBytes,
                    response.getStatus()
            );
        }
    }
}
