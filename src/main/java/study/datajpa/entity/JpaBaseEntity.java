package study.datajpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * Auditing(엔티티를 생성, 변경할 때 변경한 사람,시간 등을 추적)하기 위한 클래스 (순수 JPA 사용 버전)
 */
@Getter @Setter
@MappedSuperclass
public class JpaBaseEntity {
    @Column(updatable = false)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    @PrePersist // Persist 전에 이벤트 발생
    public void prePersist(){
        LocalDateTime now = LocalDateTime.now();
        createdDate = now;
        updatedDate = now;
    }

    @PreUpdate // Update 전에 이벤트 발생
    public void preUpdate(){
        updatedDate = LocalDateTime.now();
    }
}
