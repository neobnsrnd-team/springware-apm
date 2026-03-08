package kr.springware.profiler.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = {"kr.springware.profiler.core", "kr.springware.profiler.demo"})
@EnableScheduling
public class ProfilerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProfilerDemoApplication.class, args);
    }

}
