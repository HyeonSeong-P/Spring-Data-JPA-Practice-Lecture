package study.datajpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;
import java.util.UUID;

@EnableJpaAuditing // 스프링 데이터 JPA를 이용해 Auditing을 사용하기 위한 필수 어노테이션
@SpringBootApplication
// @EnableJpaRepositories(basePackages = "study.datajpa.repository") 스프링 부트 사용 시 JavaConfig 설정 생략 가능
public class DataJpaApplication {

	public static void main(String[] args) {
		SpringApplication.run(DataJpaApplication.class, args);
	}

	/**
	 * 등록자, 수정자를 처리해주는 AuditorAware 스프링 빈 등록
	 * 실무에선 세션 정보나 스프링 시큐리티 로그인 정보에서 ID를 받음.
	 * 여기서 등록자, 수정자에 값을 채워준다.
	 */
	@Bean
	public AuditorAware<String> auditorProvider(){
		return () -> Optional.of(UUID.randomUUID().toString());
	}

}
