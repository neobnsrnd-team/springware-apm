package kr.springware.profiler.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * APM 프로파일러 데모 애플리케이션의 진입점 클래스.
 *
 * <p>이 애플리케이션은 Spring Boot 기반의 프로파일링 데모로,
 * CPU 과부하, 메모리 누수, 느린 실행 등 다양한 성능 시나리오를 시뮬레이션합니다.</p>
 *
 * <p>{@code @SpringBootApplication} 애노테이션은 다음 기능을 포함합니다:</p>
 * <ul>
 *   <li>{@code @Configuration} - 빈(Bean) 정의 소스 지정</li>
 *   <li>{@code @EnableAutoConfiguration} - Spring Boot 자동 설정 활성화</li>
 *   <li>{@code @ComponentScan} - 현재 패키지 이하의 컴포넌트 자동 탐색</li>
 * </ul>
 */
@SpringBootApplication
public class ProfilerDemoApplication {

    /**
     * 애플리케이션을 시작하는 메인 메서드.
     *
     * <p>Spring Boot 컨텍스트를 초기화하고 내장 웹 서버를 구동합니다.</p>
     *
     * @param args 커맨드라인 인수 (Spring Boot 설정 오버라이드에 사용 가능)
     */
    public static void main(String[] args) {
        SpringApplication.run(ProfilerDemoApplication.class, args);
    }

}
