package kr.springware.profiler.core.dashboard;

import kr.springware.profiler.core.config.ProfilerConfig;
import kr.springware.profiler.core.model.IssueCategory;
import kr.springware.profiler.core.model.IssueSeverity;
import kr.springware.profiler.core.model.ProfileEvent;
import kr.springware.profiler.core.monitor.ActiveThreadTracker;
import kr.springware.profiler.core.monitor.CpuMonitor;
import kr.springware.profiler.core.monitor.MemoryMonitor;
import kr.springware.profiler.core.store.ProfileEventStore;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiler")
public class DashboardController {

    private final ProfileEventStore store;
    private final CpuMonitor cpuMonitor;
    private final MemoryMonitor memoryMonitor;
    private final ProfilerConfig config;
    private final ActiveThreadTracker threadTracker;

    public DashboardController(ProfileEventStore store, CpuMonitor cpuMonitor,
                               MemoryMonitor memoryMonitor, ProfilerConfig config,
                               ActiveThreadTracker threadTracker) {
        this.store = store;
        this.cpuMonitor = cpuMonitor;
        this.memoryMonitor = memoryMonitor;
        this.config = config;
        this.threadTracker = threadTracker;
    }

    @GetMapping("/events")
    public List<ProfileEvent> getEvents(
            @RequestParam(required = false) IssueCategory category,
            @RequestParam(required = false) IssueSeverity severity) {
        return store.getFiltered(category, severity);
    }

    @GetMapping("/summary")
    public Map<String, Long> getSummary() {
        return store.getSummary();
    }

    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("heapUsedMb", memoryMonitor.getHeapUsedMb());
        status.put("heapMaxMb", memoryMonitor.getHeapMaxMb());
        status.put("heapPercent", Math.round(memoryMonitor.getHeapUsagePercent() * 10.0) / 10.0);
        status.put("cpuProcess", Math.round(cpuMonitor.getProcessCpuLoad() * 10.0) / 10.0);
        status.put("cpuSystem", Math.round(cpuMonitor.getSystemCpuLoad() * 10.0) / 10.0);
        status.put("eventCount", store.size());
        status.put("collectMode", config.getCollectMode());
        status.put("activeThreads", threadTracker.getActiveCount());
        return status;
    }

    @GetMapping("/threads")
    public List<Map<String, Object>> getActiveThreads() {
        return threadTracker.getActiveThreadDumps();
    }

    @PostMapping("/collect-mode")
    public Map<String, String> setCollectMode(@RequestParam String mode) {
        config.setCollectMode(mode);
        return Map.of("collectMode", config.getCollectMode());
    }

    @DeleteMapping("/events")
    public Map<String, String> clearEvents() {
        store.clear();
        return Map.of("status", "cleared");
    }
}
