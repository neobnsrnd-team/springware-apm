package kr.springware.profiler.demo.problem.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MemoryLeakService {

    private static final List<byte[]> leakedChunks = new ArrayList<>();
    private static final Map<String, byte[]> cache = new HashMap<>();

    public int leak(int chunks, int chunkSizeKb) {
        for (int i = 0; i < chunks; i++) {
            leakedChunks.add(new byte[chunkSizeKb * 1024]);
        }
        return leakedChunks.size();
    }

    public int allocateLargeObject(int sizeMb) {
        byte[] large = new byte[sizeMb * 1024 * 1024];
        large[0] = 1;
        large[large.length - 1] = 1;
        return large.length;
    }

    public int cacheLeak(String key) {
        cache.put(key, new byte[1024 * 1024]);
        return cache.size();
    }

    public void reset() {
        leakedChunks.clear();
        cache.clear();
        System.gc();
    }

    public int getLeakedChunkCount() {
        return leakedChunks.size();
    }

    public int getCacheSize() {
        return cache.size();
    }
}
