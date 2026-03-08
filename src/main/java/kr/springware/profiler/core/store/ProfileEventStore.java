package kr.springware.profiler.core.store;

import kr.springware.profiler.core.config.ProfilerConfig;
import kr.springware.profiler.core.model.IssueCategory;
import kr.springware.profiler.core.model.IssueSeverity;
import kr.springware.profiler.core.model.ProfileEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Component
public class ProfileEventStore {

    private final ConcurrentLinkedDeque<ProfileEvent> events = new ConcurrentLinkedDeque<>();
    private final ProfilerConfig config;

    public ProfileEventStore(ProfilerConfig config) {
        this.config = config;
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
}
