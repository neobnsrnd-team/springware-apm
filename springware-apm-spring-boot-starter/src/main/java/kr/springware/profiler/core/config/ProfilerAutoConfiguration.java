package kr.springware.profiler.core.config;

import kr.springware.profiler.core.dashboard.DashboardController;
import kr.springware.profiler.core.detector.ThresholdDetector;
import kr.springware.profiler.core.filter.ProfilingFilter;
import kr.springware.profiler.core.monitor.CpuMonitor;
import kr.springware.profiler.core.monitor.MemoryMonitor;
import kr.springware.profiler.core.monitor.MonitoringScheduler;
import kr.springware.profiler.core.store.ProfileEventStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@EnableConfigurationProperties(ProfilerConfig.class)
@EnableScheduling
public class ProfilerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CpuMonitor cpuMonitor() {
        return new CpuMonitor();
    }

    @Bean
    @ConditionalOnMissingBean
    public MemoryMonitor memoryMonitor() {
        return new MemoryMonitor();
    }

    @Bean
    @ConditionalOnMissingBean
    public ProfileEventStore profileEventStore(ProfilerConfig config) {
        return new ProfileEventStore(config);
    }

    @Bean
    @ConditionalOnMissingBean
    public ThresholdDetector thresholdDetector(ProfilerConfig config, ProfileEventStore store, MemoryMonitor memoryMonitor) {
        return new ThresholdDetector(config, store, memoryMonitor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProfilingFilter profilingFilter(CpuMonitor cpuMonitor, MemoryMonitor memoryMonitor, ThresholdDetector detector) {
        return new ProfilingFilter(cpuMonitor, memoryMonitor, detector);
    }

    @Bean
    @ConditionalOnMissingBean
    public MonitoringScheduler monitoringScheduler(ThresholdDetector detector) {
        return new MonitoringScheduler(detector);
    }

    @Bean
    @ConditionalOnMissingBean
    public DashboardController dashboardController(ProfileEventStore store, CpuMonitor cpuMonitor,
                                                     MemoryMonitor memoryMonitor, ProfilerConfig config) {
        return new DashboardController(store, cpuMonitor, memoryMonitor, config);
    }
}
