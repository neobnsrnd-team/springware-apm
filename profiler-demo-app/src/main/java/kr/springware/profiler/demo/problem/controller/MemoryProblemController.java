package kr.springware.profiler.demo.problem.controller;

import kr.springware.profiler.demo.problem.service.MemoryLeakService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/problems/memory")
public class MemoryProblemController {

    private final MemoryLeakService service;

    public MemoryProblemController(MemoryLeakService service) {
        this.service = service;
    }

    @GetMapping("/leak")
    public Map<String, Object> leak(
            @RequestParam(defaultValue = "50") int chunks,
            @RequestParam(defaultValue = "500") int chunkSizeKb) {
        int totalChunks = service.leak(chunks, chunkSizeKb);
        return Map.of("addedChunks", chunks, "chunkSizeKb", chunkSizeKb,
                "totalChunks", totalChunks, "totalLeakedMb", totalChunks * chunkSizeKb / 1024);
    }

    @GetMapping("/large-object")
    public Map<String, Object> largeObject(@RequestParam(defaultValue = "100") int sizeMb) {
        int allocated = service.allocateLargeObject(sizeMb);
        return Map.of("sizeMb", sizeMb, "allocatedBytes", allocated);
    }

    @GetMapping("/cache-leak")
    public Map<String, Object> cacheLeak(@RequestParam(defaultValue = "abc") String key) {
        int cacheSize = service.cacheLeak(key);
        return Map.of("key", key, "cacheSize", cacheSize);
    }

    @PostMapping("/reset")
    public Map<String, String> reset() {
        service.reset();
        return Map.of("status", "reset complete");
    }
}
