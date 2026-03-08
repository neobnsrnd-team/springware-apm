package kr.springware.profiler.demo.problem.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@RestController
public class NormalController {

    @GetMapping("/api/normal")
    public Map<String, Object> normal() throws InterruptedException {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        // 랜덤 sleep 50~500ms
        long sleepMs = rand.nextLong(50, 500);
        Thread.sleep(sleepMs);

        // 적당한 객체 생성 (10~100개, 각 1~10KB)
        int count = rand.nextInt(10, 100);
        List<byte[]> objects = new ArrayList<>(count);
        int totalBytes = 0;
        for (int i = 0; i < count; i++) {
            int size = rand.nextInt(1024, 10240);
            objects.add(new byte[size]);
            totalBytes += size;
        }

        return Map.of(
                "sleepMs", sleepMs,
                "objectCount", count,
                "totalKb", totalBytes / 1024
        );
    }
}
