package kr.springware.profiler.core.config;

import kr.springware.profiler.core.dashboard.DashboardController;
import kr.springware.profiler.core.detector.ThresholdDetector;
import kr.springware.profiler.core.filter.ProfilingFilter;
import kr.springware.profiler.core.monitor.ActiveThreadTracker;
import kr.springware.profiler.core.monitor.CpuMonitor;
import kr.springware.profiler.core.monitor.MemoryMonitor;
import kr.springware.profiler.core.monitor.MonitoringScheduler;
import kr.springware.profiler.core.store.ProfileEventStore;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@EnableConfigurationProperties(ProfilerConfig.class)
@EnableScheduling
public class ProfilerAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(ProfilerAutoConfiguration.class);

    @PostConstruct
    public void init() {
        log.info("============================================================");
        log.info(" Springware APM Profiler initialized");
        log.info(" Dashboard: /apm.html");
        log.info(" API:       /api/profiler/status");
        log.info("============================================================");
    }

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
    public ActiveThreadTracker activeThreadTracker() {
        return new ActiveThreadTracker();
    }

    @Bean
    @ConditionalOnMissingBean
    public ThresholdDetector thresholdDetector(ProfilerConfig config, ProfileEventStore store, MemoryMonitor memoryMonitor) {
        return new ThresholdDetector(config, store, memoryMonitor);
    }

    @Bean
    @ConditionalOnMissingBean
    public ProfilingFilter profilingFilter(CpuMonitor cpuMonitor, MemoryMonitor memoryMonitor,
                                              ThresholdDetector detector, ActiveThreadTracker threadTracker) {
        return new ProfilingFilter(cpuMonitor, memoryMonitor, detector, threadTracker);
    }

    @Bean
    @ConditionalOnMissingBean
    public MonitoringScheduler monitoringScheduler(ThresholdDetector detector) {
        return new MonitoringScheduler(detector);
    }

    @Bean
    @ConditionalOnMissingBean
    public DashboardController dashboardController(ProfileEventStore store, CpuMonitor cpuMonitor,
                                                     MemoryMonitor memoryMonitor, ProfilerConfig config,
                                                     ActiveThreadTracker threadTracker) {
        return new DashboardController(store, cpuMonitor, memoryMonitor, config, threadTracker);
    }
}
