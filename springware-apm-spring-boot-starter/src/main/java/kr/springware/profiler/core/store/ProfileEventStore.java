package kr.springware.profiler.core.store;

import kr.springware.profiler.core.config.ProfilerConfig;
import kr.springware.profiler.core.model.IssueCategory;
import kr.springware.profiler.core.model.IssueSeverity;
import kr.springware.profiler.core.model.ProfileEvent;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ProfileEventStore {

    private final ConcurrentLinkedDeque<ProfileEvent> events = new ConcurrentLinkedDeque<>();
    private final AtomicInteger[] requestBuckets = new AtomicInteger[60];
    private volatile int currentBucket = 0;
    private volatile long lastSecond = -1;
    private final ProfilerConfig config;

    public ProfileEventStore(ProfilerConfig config) {
        this.config = config;
        for (int i = 0; i < 60; i++) {
            requestBuckets[i] = new AtomicInteger(0);
        }
    }

    private void ensureBucket() {
        long sec = System.currentTimeMillis() / 1000;
        if (sec != lastSecond) {
            lastSecond = sec;
            currentBucket = (int) (sec % 60);
            requestBuckets[currentBucket].set(0);
        }
    }

    public void add(ProfileEvent event) {
        events.addFirst(event);
        while (events.size() > config.getMaxEvents()) {
            events.removeLast();
        }
    }

    public List<ProfileEvent> getAll() {
        return List.copyOf(events);
    }

    public List<ProfileEvent> getFiltered(IssueCategory category, IssueSeverity severity) {
        return events.stream()
                .filter(e -> category == null || e.category() == category)
                .filter(e -> severity == null || e.severity() == severity)
                .toList();
    }

    public Map<String, Long> getSummary() {
        return events.stream()
                .collect(Collectors.groupingBy(
                        e -> e.category() + "_" + e.severity(),
                        Collectors.counting()));
    }

    public int size() {
        return events.size();
    }

    public void clear() {
        events.clear();
    }

    public int getTps(int windowSeconds) {
        ensureBucket();
        return requestBuckets[currentBucket].get();
    }

    public void recordRequest() {
        ensureBucket();
        requestBuckets[currentBucket].incrementAndGet();
    }
}
