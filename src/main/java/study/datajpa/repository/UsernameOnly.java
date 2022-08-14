package study.datajpa.repository;

import org.springframework.beans.factory.annotation.Value;

/**
 * [인터페이스 기반 Projections] 기능을 사용하기 위한 인터페이스
 */
public interface UsernameOnly {

    /**
     * [인터페이스 기반 Closed Projections]
     * 프로퍼티 형식(getter)의 인터페이스를 제공하면, 구현체는 스프링 데이터 JPA가 제공
     */
    String getUsername();

    /**
     * [인터페이스 기반 Opened Projections]
     * 스프링의 SpEL 문법도 지원하는데 이걸 사용하면 DB에서 엔티티 필드를 다 조회해온 다음에 계산함. 따라서 JPQL select절 최적화가 안된다.
     */
    //@Value("#{target.username + ' ' + target.age}")
    //String getUsername();
}
