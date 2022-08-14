package study.datajpa.entity;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Id;
import java.time.LocalDateTime;

/**
 * 새로운 엔티티를 구별하는 방법
 * 스프링 데이터 JPA 리포지토리의 save() 메서드
 * - 새로운 엔티티면 저장(persist)
 * - 새로운 엔티티가 아니면 병합(merge)
 *
 * JPA 식별자 생성 전략이 @Id만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 save()를 호출한다.
 * 이 경우 merge()가 호출된다. merge()는 우선 DB를 호출해서 값을 확인하고, DB에 값이 없으면 새로운 엔티티로 인지하므로 매우 비효율적이다.
 * 따라서 아래와 같이 Persistable을 사용해서 새로운 엔티티 확인 여부를 직접 구현하는게 효과적이다.
 * 참고로 등록시간(@CreatedDate)을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다.
 *
 */
@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item implements Persistable<String> {
    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    public Item(String id){
        this.id = id;
    }

    @Override
    public String getId(){
        return id;
    }

    @Override
    public boolean isNew(){
        return createdDate == null;
    }

}
