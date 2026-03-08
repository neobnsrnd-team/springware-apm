package kr.springware.profiler.core.dashboard;

import kr.springware.profiler.core.model.IssueCategory;
import kr.springware.profiler.core.model.IssueSeverity;
import kr.springware.profiler.core.model.ProfileEvent;
import kr.springware.profiler.core.monitor.CpuMonitor;
import kr.springware.profiler.core.monitor.MemoryMonitor;
import kr.springware.profiler.core.store.ProfileEventStore;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiler")
public class DashboardController {

    private final ProfileEventStore store;
    private final CpuMonitor cpuMonitor;
    private final MemoryMonitor memoryMonitor;

    public DashboardController(ProfileEventStore store, CpuMonitor cpuMonitor, MemoryMonitor memoryMonitor) {
        this.store = store;
        this.cpuMonitor = cpuMonitor;
        this.memoryMonitor = memoryMonitor;
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
        return Map.of(
                "heapUsedMb", memoryMonitor.getHeapUsedMb(),
                "heapMaxMb", memoryMonitor.getHeapMaxMb(),
                "heapPercent", Math.round(memoryMonitor.getHeapUsagePercent() * 10.0) / 10.0,
                "cpuProcess", Math.round(cpuMonitor.getProcessCpuLoad() * 10.0) / 10.0,
                "cpuSystem", Math.round(cpuMonitor.getSystemCpuLoad() * 10.0) / 10.0,
                "eventCount", store.size()
        );
    }

    @DeleteMapping("/events")
    public Map<String, String> clearEvents() {
        store.clear();
        return Map.of("status", "cleared");
    }
}
