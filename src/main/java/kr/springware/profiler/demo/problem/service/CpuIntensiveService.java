package kr.springware.profiler.demo.problem.service;

import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class CpuIntensiveService {

    public long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public int countPrimes(int upTo) {
        int count = 0;
        for (int i = 2; i <= upTo; i++) {
            if (isPrime(i)) count++;
        }
        return count;
    }

    private boolean isPrime(int n) {
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }

    public boolean regexBacktrack(int length) {
        String input = "a".repeat(length) + "!";
        return input.matches("(a+)+b");
    }

    public double tightLoop(long iterations) {
        double result = 0;
        for (long i = 0; i < iterations; i++) {
            result += Math.sin(i) * Math.cos(i) * Math.tan(i % 1000 + 1);
        }
        return result;
    }
}
