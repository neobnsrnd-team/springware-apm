# Springware APM — 업무 시스템 반입 가이드

업무 WAS에 APM 에이전트 스타터를 반입하기 위한 최소 구성 안내입니다.

## 1. 반입 대상

| 파일 | 위치 | 용도 |
|---|---|---|
| `springware-apm-spring-boot-starter-1.0.0-<빌드타임스탬프>.jar` | 이 디렉터리 | APM 에이전트 스타터 (Spring Boot Starter) |

> 파일명의 타임스탬프는 빌드 시각(`yyyyMMddHHmmss`)입니다. 어느 빌드인지 추적하기 위한 식별자입니다.

## 2. 타겟 환경

| 항목 | 지원 버전 |
|---|---|
| JDK | 17 |
| Spring Boot | 3.x (Jakarta EE) |
| WAS | JEUS 9.1 이상 (Jakarta Servlet 5.0+) / Tomcat 10+ |
| 패키징 | WAR 또는 JAR 모두 지원 |

## 3. 반입 절차

### 3-1. JAR 배치
업무 프로젝트 루트에 `libs/` 디렉터리를 만들고 JAR 파일을 복사합니다.

```
<업무 프로젝트>/
├── build.gradle
├── libs/
│   └── springware-apm-spring-boot-starter-1.0.0-<빌드타임스탬프>.jar
└── src/
```

### 3-2. Gradle 의존성 추가

`build.gradle`의 `dependencies` 블록에 다음 한 줄을 추가합니다.

```gradle
dependencies {
    // Springware APM (로컬 JAR — 버전 무관하게 매칭)
    implementation fileTree(dir: 'libs', include: ['springware-apm-spring-boot-starter-*.jar'])

    // ... 기존 의존성들 ...
}
```

> `fileTree` 패턴을 쓰는 이유: JAR의 버전이 갱신돼도 `build.gradle` 수정 없이 `libs/`의 파일만 교체하면 됩니다.

Maven을 쓰는 프로젝트라면 다음과 같이 설치합니다.

```bash
mvn install:install-file \
  -Dfile=libs/springware-apm-spring-boot-starter-1.0.0-<타임스탬프>.jar \
  -DgroupId=kr.springware \
  -DartifactId=springware-apm-spring-boot-starter \
  -Dversion=1.0.0 \
  -Dpackaging=jar
```

그리고 `pom.xml`에 의존성 추가:

```xml
<dependency>
    <groupId>kr.springware</groupId>
    <artifactId>springware-apm-spring-boot-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3-3. application.yml 설정 (선택)

설정을 생략하면 기본값으로 동작합니다. 임계치를 조정하려면 다음 블록을 추가하세요.

```yaml
# Springware APM Configuration
profiler:
  collect-mode: all             # all | issues-only
  max-events: 1000              # 메모리 링버퍼 크기
  monitoring-interval-ms: 5000  # 주기 수집 간격
  threshold:
    response-time-ms: 3000      # 응답시간 임계치 (ms)
    cpu-percent: 80             # CPU 임계치 (%)
    memory-percent: 85          # 힙 사용률 임계치 (%)
    memory-spike-mb: 50         # 요청당 힙 증가 임계치 (MB)
```

| 속성 | 기본값 | 설명 |
|---|---|---|
| `profiler.collect-mode` | `all` | `all`=모든 요청 기록, `issues-only`=임계치 초과만 기록 |
| `profiler.max-events` | `1000` | 메모리 링버퍼 최대 이벤트 수 |
| `profiler.monitoring-interval-ms` | `5000` | `MonitoringScheduler`의 주기 헬스체크 간격 |
| `profiler.threshold.response-time-ms` | `3000` | 요청 응답시간이 이 값을 넘으면 SLOW 경보 |
| `profiler.threshold.cpu-percent` | `80` | 프로세스 CPU가 이 값을 넘으면 CPU 경보 |
| `profiler.threshold.memory-percent` | `85` | 힙 사용률이 이 값을 넘으면 MEMORY 경보 |
| `profiler.threshold.memory-spike-mb` | `50` | 한 요청에서 힙이 이 값 이상 증가하면 MEMORY 경보 |

### 3-4. 빌드 & 배포

기존 배포 프로세스 그대로 WAR/JAR 빌드 후 JEUS에 재배포합니다.
- 업무 코드 수정은 **0줄**입니다.
- 기존 `@Configuration`, 필터, 인터셉터 변경 없습니다.
- 배포 산출물(WAR) 안에 APM 스타터 JAR가 함께 포함되어 나갑니다.

## 4. 반입 후 동작 확인

업무 API가 기동되는 컨텍스트 경로를 `{ctx}`라 하면(예: `/bizapp`), 다음 URL들이 활성화됩니다.

### 4-1. 헬스체크 — APM이 살아있는지 확인
```
GET http://<host>:<port>/{ctx}/api/profiler/status
```

응답 예:
```json
{
  "heapUsedMb": 150,
  "heapMaxMb": 4096,
  "heapPercent": 3.7,
  "cpuProcess": 0.5,
  "cpuSystem": 12.3,
  "eventCount": 0,
  "collectMode": "all",
  "activeThreads": 0
}
```

### 4-2. 수집된 이벤트 조회
```
GET http://<host>:<port>/{ctx}/api/profiler/events
```
업무 API를 몇 번 호출한 뒤 이 URL을 열면 요청별 측정 데이터(응답시간, CPU시간, 힙 변화량 등)가 JSON 배열로 반환됩니다.

### 4-3. 집계
```
GET http://<host>:<port>/{ctx}/api/profiler/summary
```

### 4-4. 활성 스레드 덤프
```
GET http://<host>:<port>/{ctx}/api/profiler/threads
```
현재 처리 중인 업무 API 스레드의 상태와 스택 트레이스를 반환합니다.

### 4-5. 대시보드 (내장 HTML)
```
http://<host>:<port>/{ctx}/apm.html
```
xLog scatter chart, CPU/Memory 라인 차트, 이벤트 리스트, 스레드 덤프 모달을 포함한 실시간 대시보드입니다.

## 5. 제공되는 REST API 전체 목록

| Method | 경로 | 용도 |
|---|---|---|
| `GET` | `/api/profiler/status` | 현재 JVM 상태 스냅샷 |
| `GET` | `/api/profiler/events` | 수집된 이벤트 전체 (필터 파라미터: `category`, `severity`) |
| `GET` | `/api/profiler/summary` | 카테고리/심각도별 집계 |
| `GET` | `/api/profiler/threads` | 진행 중인 API 스레드 덤프 |
| `POST` | `/api/profiler/collect-mode?mode=all\|issues-only` | 수집 모드 런타임 변경 |
| `DELETE` | `/api/profiler/events` | 이벤트 히스토리 초기화 |

## 6. 동작 원리 개요

### 자동 등록
JAR 안의 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`가 Spring Boot의 AutoConfiguration 메커니즘을 통해 다음 Bean들을 자동 등록합니다.

- `ProfilingFilter` — `OncePerRequestFilter`(HIGHEST_PRECEDENCE)로 모든 요청 계측
- `CpuMonitor` — `ThreadMXBean`/`OperatingSystemMXBean`으로 CPU 측정
- `MemoryMonitor` — `MemoryMXBean`으로 힙 사용량 측정
- `ActiveThreadTracker` — 현재 처리 중인 요청 스레드 추적
- `ThresholdDetector` — 임계치 기반 심각도 판정
- `ProfileEventStore` — 메모리 링버퍼 (`profiler.max-events` 개수 제한)
- `MonitoringScheduler` — `@Scheduled` 기반 주기 헬스체크
- `DashboardController` — `/api/profiler/**` REST 엔드포인트

### 데이터 저장
- **메모리 링버퍼**에만 저장됩니다. DB나 파일 없음.
- WAS 재기동 시 이벤트는 초기화됩니다 (실시간 관제 용도).
- 최근 N건(`profiler.max-events`) 유지, 초과 시 오래된 것부터 자동 폐기.

### 성능 오버헤드
- 요청당 추가 비용: `ThreadMXBean.getThreadCpuTime()` 시스템콜 2회, 힙 사용량 조회 2회, 간단한 Map put/remove.
- TPS가 높은 업무에선 `collect-mode=issues-only`로 시작 권장.

## 7. 보안 주의사항

현재 버전은 **대시보드에 인증이 없습니다**. 다음 중 하나 이상의 보호 장치가 반드시 필요합니다.

1. **네트워크 경계 보호** — JEUS `jeus-web-dd.xml`에 `<security-constraint>`로 `/api/profiler/*` 제한
2. **관리자 콘솔 경유 접근** — 업무 WAS로는 관리자 콘솔 IP만 허용, 운영자는 관리자 콘솔 메뉴를 통해서만 조회
3. **기동 토글** — `profiler.enabled=false`를 기본값으로 두고 필요 시에만 ON (차기 버전 예정)

권장 운영 구성:

```
[관리자 콘솔 (인증)]  ──폴링──►  [업무 WAS의 /api/profiler/**]
                                  ↑ IP 화이트리스트로 관리자 콘솔만 허용
```

## 8. 해제 방법

문제 발생 시 원복은 다음 두 단계로 완료됩니다.

1. `build.gradle`에서 의존성 1줄 삭제
2. 재배포

업무 코드는 원래부터 건드린 곳이 없으므로 되돌릴 것이 없습니다.

## 9. 문의 및 이슈

- 저장소: https://github.com/neobnsrnd-team/springware-apm
- 이 가이드의 버전: `1.0.0-<빌드타임스탬프>` (JAR 파일명과 동일)
