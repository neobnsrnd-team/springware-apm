package kr.springware.profiler.demo.problem.service;

import kr.springware.profiler.demo.problem.mapper.DemoMapper;
import kr.springware.profiler.demo.problem.model.DemoItem;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SlowExecutionService {

    private final DemoMapper demoMapper;
    private final Object lock = new Object();

    public SlowExecutionService(DemoMapper demoMapper) {
        this.demoMapper = demoMapper;
    }

    public void sleep(long delayMs) throws InterruptedException {
        Thread.sleep(delayMs);
    }

    public List<DemoItem> nPlusOneQuery() {
        List<String> categories = demoMapper.findAllCategories();
        List<DemoItem> allItems = new ArrayList<>();
        for (String category : categories) {
            allItems.addAll(demoMapper.findByCategory(category));
        }
        return allItems;
    }

    public String synchronizedWork(long workMs) throws InterruptedException {
        synchronized (lock) {
            Thread.sleep(workMs);
            return "Completed synchronized work after " + workMs + "ms";
        }
    }
}
