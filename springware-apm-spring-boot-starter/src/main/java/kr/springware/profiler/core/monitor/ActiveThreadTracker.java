package kr.springware.profiler.core.monitor;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActiveThreadTracker {

    private final ConcurrentHashMap<Long, RequestInfo> activeRequests = new ConcurrentHashMap<>();
    private final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

    public void register(String endpoint) {
        Thread t = Thread.currentThread();
        activeRequests.put(t.threadId(), new RequestInfo(
                t.threadId(), t.getName(), endpoint, System.currentTimeMillis()));
    }

    public void unregister() {
        activeRequests.remove(Thread.currentThread().threadId());
    }

    public int getActiveCount() {
        return activeRequests.size();
    }

    public List<Map<String, Object>> getActiveThreadDumps() {
        List<Map<String, Object>> result = new ArrayList<>();
        long now = System.currentTimeMillis();

        for (RequestInfo info : activeRequests.values()) {
            ThreadInfo ti = threadMXBean.getThreadInfo(info.threadId, 32);
            if (ti == null) continue;

            StringBuilder sb = new StringBuilder();
            for (StackTraceElement ste : ti.getStackTrace()) {
                sb.append("  at ").append(ste).append("\n");
            }

            result.add(Map.of(
                    "threadId", info.threadId,
                    "threadName", info.threadName,
                    "endpoint", info.endpoint,
                    "elapsedMs", now - info.startTime,
                    "threadState", ti.getThreadState().name(),
                    "stackTrace", sb.toString()
            ));
        }

        result.sort((a, b) -> Long.compare((long) b.get("elapsedMs"), (long) a.get("elapsedMs")));
        return result;
    }

    private record RequestInfo(long threadId, String threadName, String endpoint, long startTime) {}
}
