# Springware APM

Multi-module Gradle project:
- `springware-apm-spring-boot-starter` — reusable profiler library (auto-configuration via `META-INF/spring/...AutoConfiguration.imports`)
- `profiler-demo-app` — demo Spring Boot app

## Developer Commands

```bash
./gradlew bootRun                           # Start demo app (port 8080)
./gradlew test                              # Run all tests
./gradlew :profiler-demo-app:test           # Run demo app tests only
./gradlew build                             # Build all modules
./gradlew :springware-apm-spring-boot-starter:build  # Build starter only
./gradlew test --tests "ProfilerIntegrationTest.*"   # Run single test class
```

## Tech Stack

- Spring Boot 3.4.3 / Java 17 / Gradle 9.3.1
- MyBatis 3.0.4 / H2 (in-memory)
- JUnit 5, TestRestTemplate

## Package Structure

```
kr.springware.profiler
├── core/                          # Profiler library
│   ├── config/ProfilerConfig      # @ConfigurationProperties
│   ├── filter/ProfilingFilter     # OncePerRequestFilter (HIGHEST_PRECEDENCE)
│   ├── detector/ThresholdDetector
│   ├── monitor/                   # CpuMonitor, MemoryMonitor, ActiveThreadTracker
│   ├── store/ProfileEventStore    # Thread-safe ring buffer
│   └── dashboard/DashboardController  # /api/profiler/**
└── demo/                          # Demo app
    ├── problem/                   # CPU/Slow/Memory test endpoints
    └── mapper/DemoMapper
```

## Testing Notes

- Tests live in `profiler-demo-app/src/test` only (starter module has no tests)
- Integration tests use embedded H2, no external services needed
- Problem endpoints for testing: `/api/problems/cpu/*`, `/api/problems/slow/*`, `/api/problems/memory/*`

## Versioning

Version = `1.0.0-<yyyyMMddHHmmss>` (build timestamp embedded automatically).

## IVE

See `CLAUDE.md` for IVE issue creation rules (Korean).