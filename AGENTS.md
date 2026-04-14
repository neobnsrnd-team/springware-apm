# Springware APM

## Project Structure

Multi-module Gradle project with 2 subprojects:
- `springware-apm-spring-boot-starter` - Reusable profiler library
- `profiler-demo-app` - Demo Spring Boot application

Entry point: `./gradlew bootRun`

## Developer Commands

```bash
./gradlew bootRun              # Start demo app on port 8080
./gradlew test                # Run all tests
./gradlew :profiler-demo-app:test  # Run demo app tests only
./gradlew build              # Build all modules
./gradlew :springware-apm-spring-boot-starter:build  # Build starter only
```

## Run Single Test

```bash
./gradlew test --tests "ProfilerIntegrationTest.fibonacci_shouldTriggerCpuAlert"
```

## Tech Stack

- Spring Boot 3.4.3 / Java 17
- Gradle 9.3.1
- MyBatis 3.0.4 / H2 (in-memory)
- JUnit 5

## Package Structure

```
kr.springware.profiler
├── core/                          # Reusable profiler engine
│   ├── config/ProfilerConfig      # @ConfigurationProperties
│   ├── filter/ProfilingFilter     # Per-request servlet filter
│   ├── detector/ThresholdDetector # Threshold evaluation
│   ├── monitor/                   # CPU, memory monitors
│   ├── store/ProfileEventStore    # Thread-safe event ring buffer
│   └── dashboard/DashboardController
└── demo/                          # Demo application
    ├── problem/                   # CPU/Slow/Memory problem endpoints
    └── mapper/DemoMapper          # MyBatis mapper
```

## Testing

Tests are in `profiler-demo-app/src/test`. Integration tests use embedded H2 and mock servers as needed.

## IVE

This repo is managed by Spring IVE. When user requests "IVE용 이슈로 정리해줘":
1. Create one Git issue per logical task unit
2. Attach labels `IVE`, `IVE-E`
3. Keep issues open (IVE scanner auto-closes after processing)
4. Include: changed files, commit SHA, summary in body

<!-- ive:begin -->
This project is a **Spring IVE** managed target.

### Issue Creation Rules

1. Create **one Git issue per task unit** using GitHub/GitLab API or `gh`/`glab` CLI
2. Attach labels `IVE`, `IVE-E` - leave issue **open** (not closed)
3. Title: concise (shown in IVE report)
4. Body: changed files, related commit SHA, summary (markdown)

### Optional Metadata

Add at end of issue body:
```html
<!-- ive-meta
model: <model-name>
input_tokens: <number>
output_tokens: <number>
cost_usd: <number>
turns: <number>
started_at: <ISO-timestamp>
ended_at: <ISO-timestamp>
-->
```

### Notes

- No IVE server address or token needed
- IVE auto-detects Git issues
- Show issue number and URL to user after creation
<!-- ive:end -->