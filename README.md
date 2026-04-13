# Springware APM - xLog Tracer

A lightweight Java profiler with a Scouter APM-style **xLog scatter chart** dashboard. Detects CPU, slow execution, and memory issues in real-time via servlet filter profiling, periodic monitoring, and threshold-based alerting. Includes **CPU/Memory/Active Threads line chart** and **live thread dump** viewer for API-processing threads.

## xLog Dashboard

![xLog Dashboard](docs/xlog-dashboard.png)

### Demo

![xLog Demo](docs/xlog-demo.gif)

## Features

- **Per-request profiling** via servlet filter (`ThreadMXBean` CPU time, heap delta, elapsed time)
- **Periodic memory monitoring** with `@Scheduled` system heap checks
- **Threshold-based alerting** with severity escalation (WARNING / CRITICAL)
- **xLog scatter chart** - real-time response time visualization (X=time, Y=elapsed)
- **CPU/Memory/Threads line chart** - 2-minute sliding window with dual Y-axes (% and thread count)
- **Active thread dump** - click Threads pill or legend to view live stack traces of API-processing threads
- **Color-coded events** - Normal (cyan), Warning (orange), Critical (red), Error (magenta)
- **Trigger buttons** to fire intentional CPU, slow execution, and memory problems
- **Reusable core module** (`kr.springware.profiler.core`) separated from demo app

## Tech Stack

- Spring Boot 3.4.3 / Java 17
- MyBatis 3.0.4 / H2 (in-memory)
- Vanilla HTML/CSS/JS dashboard (no frontend framework)
- Gradle 9.3.1

## Quick Start

```bash
./gradlew bootRun
```

Open the dashboard at [http://localhost:8080/api/profiler/dashboard](http://localhost:8080/api/profiler/dashboard).

> The dashboard HTML is served directly by `DashboardController`, so it works under any deployment context path and in environments where serving static resources is restricted.

## Problem Endpoints

| Category | Endpoint | Description |
|----------|----------|-------------|
| CPU | `GET /api/problems/cpu/fibonacci?n=42` | Recursive fibonacci (exponential) |
| CPU | `GET /api/problems/cpu/primes?upTo=500000` | Trial division primes |
| CPU | `GET /api/problems/cpu/tight-loop?iterations=100000000` | Math-heavy loop |
| Slow | `GET /api/problems/slow/sleep?delayMs=5000` | Thread.sleep |
| Slow | `GET /api/problems/slow/n-plus-one` | N+1 query pattern |
| Slow | `GET /api/problems/slow/synchronized?workMs=3000` | Lock contention |
| Memory | `GET /api/problems/memory/leak?chunks=50&chunkSizeKb=500` | Static list leak |
| Memory | `GET /api/problems/memory/large-object?sizeMb=100` | Large allocation |
| Memory | `GET /api/problems/memory/cache-leak?key=abc` | Unbounded cache |
| Memory | `POST /api/problems/memory/reset` | Cleanup leaked memory |

## Profiler API

| Endpoint | Description |
|----------|-------------|
| `GET /api/profiler/dashboard` | xLog dashboard HTML (served by controller, context-path safe) |
| `GET /api/profiler/events` | List all profiler events (filterable by category/severity) |
| `GET /api/profiler/summary` | Aggregated counts by category and severity |
| `GET /api/profiler/status` | Live system health (heap, CPU, active threads, event count) |
| `GET /api/profiler/threads` | Active API thread dump (endpoint, elapsed, state, stack trace) |
| `POST /api/profiler/collect-mode?mode=all\|issues-only` | Change collection mode at runtime |
| `DELETE /api/profiler/events` | Clear event history |

## Test Results

```
> Task :test

ProfilerDemoApplicationTests
  contextLoads()                              PASSED

ProfilerIntegrationTest
  fibonacci_shouldTriggerCpuAlert()           PASSED
  primes_shouldTriggerCpuAlert()              PASSED
  sleep_shouldTriggerSlowExecutionAlert()     PASSED
  nPlusOne_shouldTriggerSlowExecutionAlert()  PASSED
  memoryLeak_shouldTriggerMemoryAlert()       PASSED
  largeAllocation_shouldTriggerMemoryAlert()  PASSED
  dashboard_shouldReturnEvents()              PASSED
  dashboard_shouldReturnStatus()              PASSED

BUILD SUCCESSFUL - 9 tests completed, 0 failed
```

## Package Structure

```
kr.springware.profiler
├── core/                          # Reusable profiler engine
│   ├── config/ProfilerConfig      # @ConfigurationProperties thresholds
│   ├── filter/ProfilingFilter     # Per-request servlet filter
│   ├── detector/ThresholdDetector # Threshold evaluation & severity
│   ├── monitor/                   # CPU, memory & active thread monitors
│   ├── store/ProfileEventStore    # Thread-safe event ring buffer
│   ├── model/                     # ProfileEvent, enums
│   └── dashboard/DashboardController  # REST API
└── demo/                          # Demo application
    ├── ProfilerDemoApplication    # @SpringBootApplication
    └── problem/                   # Intentional problem endpoints
        ├── controller/            # CPU, Slow, Memory controllers
        ├── service/               # Problem implementations
        ├── mapper/DemoMapper      # MyBatis mapper
        └── model/DemoItem         # Demo entity
```

## Configuration

```properties
# Thresholds
profiler.threshold.response-time-ms=3000   # Slow response threshold (default: 3000ms)
profiler.threshold.cpu-percent=80           # CPU usage alert threshold (default: 80%)
profiler.threshold.memory-percent=85        # Heap usage alert threshold (default: 85%)
profiler.threshold.memory-spike-mb=50       # Memory spike threshold (default: 50MB)

# Monitoring
profiler.monitoring-interval-ms=5000        # System health check interval (default: 5000ms)
profiler.max-events=1000                    # Max events in ring buffer (default: 1000)
profiler.collect-mode=all                   # "all" or "issues-only" (default: all)
```

| Property | Default | Description |
|----------|---------|-------------|
| `profiler.threshold.response-time-ms` | `3000` | Requests exceeding this are flagged as slow |
| `profiler.threshold.cpu-percent` | `80` | Per-request CPU usage alert threshold |
| `profiler.threshold.memory-percent` | `85` | Heap usage percentage alert threshold |
| `profiler.threshold.memory-spike-mb` | `50` | Per-request heap delta alert threshold |
| `profiler.monitoring-interval-ms` | `5000` | Periodic system health check interval |
| `profiler.max-events` | `1000` | Maximum events kept in ring buffer |
| `profiler.collect-mode` | `all` | `all` records every request, `issues-only` records only threshold violations |
