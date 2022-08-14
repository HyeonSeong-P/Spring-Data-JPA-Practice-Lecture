package study.datajpa.repository;

import lombok.RequiredArgsConstructor;
import study.datajpa.entity.Member;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * [사용자 정의 구현 클래스]
 * 규칙:
 * - 리포지토리 인터페이스 이름 + "Impl" // ex. MemberRepositoryImpl
 * - (스프링 2.x 이상 추가 지원) 사용자 정의 인터페이스 이름 + "Impl" // ex. MemberRepositoryCustomImpl
 *  - 기존 방식보다 이 방식이 사용자 정의 인터페이스 이름과 구현 클래스 이름이 비슷하므로 더 직관적이다.
 *  - 여러 인터페이스를 분리해서 구현하는 것도 가능하다.
 *  - 따라서 이 방식을 사용하는 것을 권장한다.
 *
 * 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록
 *
 * 실무에선 주로 QueryDSL이나 SpringJdbcTemplate을 함께 사용할 때 사용자 정의 리포지토리 기능 자주 사용
 *
 * 항상 사용자 정의 리포지토리가 필요한 것은 아니다. 그냥 임의의 리포지토리를 만들어도 된다.
 * 예를 들어 화면에 쓰이는 복잡한 쿼리가 필요할 때 MemberQueryRepository 클래스를 만들어 스프링 빈으로 등록해 직접 사용해도 된다.
 * 물론 이 경우 스프링 데이터 JPA와는 관계 없이 별도로 동작한다.
 */
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Member> findMemberCustom() {
        return em.createQuery("select m from Member m").getResultList();
    }
}
