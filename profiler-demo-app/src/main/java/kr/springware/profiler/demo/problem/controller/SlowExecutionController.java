package kr.springware.profiler.demo.problem.controller;

import kr.springware.profiler.demo.problem.service.SlowExecutionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/problems/slow")
public class SlowExecutionController {

    private final SlowExecutionService service;

    public SlowExecutionController(SlowExecutionService service) {
        this.service = service;
    }

    @GetMapping("/sleep")
    public Map<String, Object> sleep(@RequestParam(defaultValue = "5000") long delayMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        service.sleep(delayMs);
        return Map.of("delayMs", delayMs, "actualMs", System.currentTimeMillis() - start);
    }

    @GetMapping("/n-plus-one")
    public Map<String, Object> nPlusOne() {
        long start = System.currentTimeMillis();
        var items = service.nPlusOneQuery();
        return Map.of("itemCount", items.size(), "durationMs", System.currentTimeMillis() - start);
    }

    @GetMapping("/synchronized")
    public Map<String, Object> synchronizedWork(@RequestParam(defaultValue = "3000") long workMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        String result = service.synchronizedWork(workMs);
        return Map.of("result", result, "durationMs", System.currentTimeMillis() - start);
    }
}
