package kr.springware.profiler.demo;

import kr.springware.profiler.core.model.IssueCategory;
import kr.springware.profiler.core.model.IssueSeverity;
import kr.springware.profiler.core.model.ProfileEvent;
import kr.springware.profiler.core.store.ProfileEventStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProfilerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    ProfileEventStore eventStore;

    @BeforeEach
    void setUp() {
        eventStore.clear();
    }

    @Test
    void fibonacci_shouldTriggerCpuAlert() {
        restTemplate.getForObject("/api/problems/cpu/fibonacci?n=40", String.class);
        List<ProfileEvent> cpuEvents = eventStore.getFiltered(IssueCategory.CPU, null);
        assertFalse(cpuEvents.isEmpty(), "Should detect CPU issue from fibonacci");
    }

    @Test
    void primes_shouldTriggerCpuAlert() {
        restTemplate.getForObject("/api/problems/cpu/primes?upTo=2000000", String.class);
        List<ProfileEvent> cpuEvents = eventStore.getFiltered(IssueCategory.CPU, null);
        assertFalse(cpuEvents.isEmpty(), "Should detect CPU issue from primes");
    }

    @Test
    void sleep_shouldTriggerSlowExecutionAlert() {
        restTemplate.getForObject("/api/problems/slow/sleep?delayMs=4000", String.class);
        List<ProfileEvent> slowEvents = eventStore.getFiltered(IssueCategory.SLOW_EXECUTION, IssueSeverity.WARNING);
        assertFalse(slowEvents.isEmpty(), "Should detect slow execution from sleep");
    }

    @Test
    void nPlusOne_shouldTriggerSlowExecutionAlert() {
        restTemplate.getForObject("/api/problems/slow/n-plus-one", String.class);
        assertTrue(eventStore.size() > 0, "Should record events for N+1 query");
    }

    @Test
    void memoryLeak_shouldTriggerMemoryAlert() {
        restTemplate.getForObject("/api/problems/memory/leak?chunks=100&chunkSizeKb=1024", String.class);
        List<ProfileEvent> memEvents = eventStore.getFiltered(IssueCategory.MEMORY, null);
        assertFalse(memEvents.isEmpty(), "Should detect memory spike from leak");
    }

    @Test
    void largeAllocation_shouldTriggerMemoryAlert() {
        restTemplate.getForObject("/api/problems/memory/large-object?sizeMb=200", String.class);
        List<ProfileEvent> memEvents = eventStore.getFiltered(IssueCategory.MEMORY, null);
        assertFalse(memEvents.isEmpty(), "Should detect memory spike from large allocation");
    }

    @Test
    void dashboard_shouldReturnEvents() {
        restTemplate.getForObject("/api/problems/slow/sleep?delayMs=4000", String.class);
        String events = restTemplate.getForObject("/api/profiler/events", String.class);
        assertNotNull(events);
        assertTrue(events.contains("SLOW_EXECUTION"), "Dashboard API should return events");
    }

    @Test
    void dashboard_shouldReturnStatus() {
        String status = restTemplate.getForObject("/api/profiler/status", String.class);
        assertNotNull(status);
        assertTrue(status.contains("heapUsedMb"), "Status should contain heap info");
    }
}
