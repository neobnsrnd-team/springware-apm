package kr.springware.profiler.core.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.springware.profiler.core.detector.ThresholdDetector;
import kr.springware.profiler.core.monitor.CpuMonitor;
import kr.springware.profiler.core.monitor.MemoryMonitor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProfilingFilter extends OncePerRequestFilter {

    private final CpuMonitor cpuMonitor;
    private final MemoryMonitor memoryMonitor;
    private final ThresholdDetector detector;

    public ProfilingFilter(CpuMonitor cpuMonitor, MemoryMonitor memoryMonitor, ThresholdDetector detector) {
        this.cpuMonitor = cpuMonitor;
        this.memoryMonitor = memoryMonitor;
        this.detector = detector;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/profiler")
                || path.startsWith("/h2-console")
                || path.equals("/")
                || path.startsWith("/static")
                || path.endsWith(".html")
                || path.endsWith(".css")
                || path.endsWith(".js")
                || path.endsWith(".ico");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        long startCpuTime = cpuMonitor.getCurrentThreadCpuTimeNanos();
        long startHeap = memoryMonitor.getHeapUsage().getUsed();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long elapsedMs = System.currentTimeMillis() - startTime;
            long cpuTimeNanos = cpuMonitor.getCurrentThreadCpuTimeNanos() - startCpuTime;
            long heapDelta = memoryMonitor.getHeapUsage().getUsed() - startHeap;

            String endpoint = request.getMethod() + " " + request.getRequestURI();
            if (request.getQueryString() != null) {
                endpoint += "?" + request.getQueryString();
            }

            detector.evaluateRequest(
                    endpoint,
                    request.getMethod(),
                    elapsedMs,
                    cpuTimeNanos,
                    heapDelta,
                    response.getStatus()
            );
        }
    }
}
