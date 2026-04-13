package kr.springware.profiler.core.config;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * AutoConfiguration이 동작하지 않는 환경에서 수동으로 APM을 활성화합니다.
 *
 * <pre>
 * {@literal @}SpringBootApplication
 * {@literal @}EnableSpringwareApm
 * public class MyApplication { ... }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(ProfilerAutoConfiguration.class)
public @interface EnableSpringwareApm {
}
