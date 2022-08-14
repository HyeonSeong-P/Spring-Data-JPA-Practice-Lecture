package study.datajpa.entity;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

/**
 * Auditing(엔티티를 생성, 변경할 때 변경한 사람,시간 등을 추적)하기 위한 클래스 (스프링 데이터 JPA 사용 버전)
 *
 * 실무에서 대부분의 엔티티는 등록시간, 수정시간이 필요하지만 등록자, 수정자는 없을 수도 있다.
 * 그래서 다음과 같이 Base 타입을 분리하고 원하는 타입을 선택해서 상속한다.
 */
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
@Getter
public class BaseTimeEntity {
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
