package kr.springware.profiler.demo.problem.controller;

import kr.springware.profiler.demo.problem.service.CpuIntensiveService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/problems/cpu")
public class CpuProblemController {

    private final CpuIntensiveService service;

    public CpuProblemController(CpuIntensiveService service) {
        this.service = service;
    }

    @GetMapping("/fibonacci")
    public Map<String, Object> fibonacci(@RequestParam(defaultValue = "42") int n) {
        long start = System.currentTimeMillis();
        long result = service.fibonacci(n);
        return Map.of("n", n, "result", result, "durationMs", System.currentTimeMillis() - start);
    }

    @GetMapping("/primes")
    public Map<String, Object> primes(@RequestParam(defaultValue = "500000") int upTo) {
        long start = System.currentTimeMillis();
        int count = service.countPrimes(upTo);
        return Map.of("upTo", upTo, "primeCount", count, "durationMs", System.currentTimeMillis() - start);
    }

    @GetMapping("/regex")
    public Map<String, Object> regex(@RequestParam(defaultValue = "25") int length) {
        long start = System.currentTimeMillis();
        boolean matched = service.regexBacktrack(length);
        return Map.of("length", length, "matched", matched, "durationMs", System.currentTimeMillis() - start);
    }

    @GetMapping("/tight-loop")
    public Map<String, Object> tightLoop(@RequestParam(defaultValue = "100000000") long iterations) {
        long start = System.currentTimeMillis();
        double result = service.tightLoop(iterations);
        return Map.of("iterations", iterations, "result", result, "durationMs", System.currentTimeMillis() - start);
    }
}
