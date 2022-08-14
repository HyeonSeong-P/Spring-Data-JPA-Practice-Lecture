package study.datajpa.repository;

import study.datajpa.dto.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import study.datajpa.entity.Member;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom { // 제너릭: 엔티티 타입, PK 타입 / 사용자 커스텀 리포지토리 상속
    /**
     * [메서드 이름으로 쿼리 생성]
     * 이렇게 인터페이스 안에 메서드 선언만 해주면 된다!!!
     * 스프링 데이터 JPA는 메서드 이름을 분석해서 JPQL을 생성하고 실행한다.
     * 자세한 메서드 관련 키워드는 스프링 데이터 JPA 공식문서를 확인 하자
    */
    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

    /**
     * [@Query를 이용하여 리포지토리 메서드에 쿼리 정의하기]
     * 애플리케이션 실행 시점에 문법 오류를 발견할 수 있는 큰 장점이 있음
     *
     * 파라미터 바인딩도 들어가있다.
     * 파라미터 바인딩은 코드 가독성과 유지보수를 위해 이름 기반 파라미터 바인딩을 사용하자
     * 위치 기반의 경우 파라미터 순서가 바뀌면 오류가 발생할 수 있다.
     */
    @Query("select m from Member m where m.username= :username and m.age = :age")
    List<Member> findUser(@Param("username") String username, @Param("age") int age);

    @Query("select m.username from Member m")
    List<String> findUsernameList();

    @Query("select new study.datajpa.dto.MemberDto(m.id,m.username,t.name) from Member m join m.team t")
    List<MemberDto> findMemberDto();

    /**
     * [컬렉션 파라미터 바인딩]
     */
    @Query("select m from Member m where m.username in :names")
    List<Member> findByNames(@Param("names") Collection<String> names);

    /**
     * 여러 반환타입을 지원, 아래 예제 이외에도 다양한 반환타입을 지원한다.
     * 조회 결과가 많거나 없을 경우
     * - 컬렉션
     *  - 결과 없음: 빈 컬렉션 반환
     * - 단건 조회
     *  - 결과 없음: null 반환
     *  - 결과가 2건 이상: javax.persistence.NonUnipueResultException 예외 발생
     */
    List<Member> findListByUsername(String name); // 컬렉션, 결과가 없으면 빈 컬렉션을 반환
    Member findMemberByUsername(String name); // 단건
    Optional<Member> findOptionalByUsername(String name); // Optional



    /**
     * [스프링 데이터 JPA 페이징과 정렬]
     * Page: 추가 count 쿼리 결과를 포함하는 페이징
     * Slice: 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적으로 limit + 1 조회)
     * List(자바 컬렉션): 추가 count 쿼리 없이 결과만 반환
     */
    //    Page<Member> findByUsername(String name, Pageable pageable); // count 쿼리 사용
//    Slice<Member> findByUsername(String name, Pageable pageable); // count 쿼리 사용 안함
//    List<Member> findByUsername(String name, Pageable pageable); // count 쿼리 사용 안함
//    List<Member> findByUsername(String name, Sort sort );

    // 쿼리가 복잡할 땐 count 성능을 향상 시키기 위해 count 쿼리를 분리하는 게 좋다.
    // sort도 복잡할 땐 value에 할당한 쿼리에 sort 쿼리도 같이 직접 작성한다.
    @Query(value = "select m from Member m left join m.team t", countQuery = "select count(m) from Member m")
    Page<Member> findByAge(int age, Pageable pageable);
    //Slice<Member> findByAge(int age, Pageable pageable);

    /**
     * [벌크성 수정 쿼리]
     * 벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용해야 함.
     * 벌크 연산은 영속성 컨텍스트를 무시하고 실행하기 때문에, 영속성 컨텍스트에 있는 엔티티의 상태와 DB에 엔티티 상태가 달라질 수 있다.
     * 따라서 한 트랜잭션에서 벌크성 연산 후 데이터를 다시 조회하려면
     * EntityManager 의 clear 메서드나 clearAutomatically = true 옵션으로 벌크성 쿼리를 실행 후 영속성 컨텍스트를 초기화해야 함.
     */
    @Modifying(clearAutomatically = true) // clearAutomatically = true: 벌크 쿼리를 실행하고 나서 영속성 컨텍스트 초기화
    @Query("update Member m set m.age = m.age + 1 where m.age >= :age")
    int bulkAgePlus(@Param("age") int age);


    /**
     * [@EntityGraph]
     * 순수 JPA의 경우 연관된 엔티티를 한번에 조회하려면 페치 조인을 이용한 JPQL 쿼리 작성이 필요하다.
     * 스프링 데이터 JPA는 JPA가 제공하는 엔티티 그래프 기능을 편리하게 사용하게 도와준다.
     * 이 기능을 사용하면 JPQL 없이 페치 조인을 사용할 수 있다(JPQL + 엔티티 그래프 도 가능)
     * 기본적으로 간단할때 이 기능을 사용하고 복잡한 경우엔 그냥 직접 페치 조인을 사용한다.
     */
    @Query("select m from Member m left join fetch m.team") // 페치 조인을 사용에 Member와 연관된 Team 한번에 같이 들고 옴.
    List<Member> findMemberFetchJoin();

    // 공통 메서드 오버라이드
    @Override
    @EntityGraph(attributePaths = {"team"})
    List<Member> findAll();

    // JPQL + 엔티티 그래프
    @EntityGraph(attributePaths = {"team"})
    @Query("select m from Member m")
    List<Member> findMemberEntityGraph();

    // 메서드 이름으로 쿼리에서 특히 편리하다
    @EntityGraph(attributePaths = {"team"})
    List<Member> findByUsername(String username);

    /**
     * [QueryHint}
     */
    @QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value = "true"))
    Member findReadOnlyByUsername(String username);

    /**
     * [Lock]
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Member> findLockByUsername(String name);

    /**
     * [Projections]
     * - 인터페이스 기반 Closed Projections (UsernameOnly 인터페이스)
     * - 인터페이스 기반 Opened Projections (UsernameOnly 인터페이스)
     * - 클래스 기반 Projections (UsernameOnlyDto 클래스)
     */
    List<UsernameOnly> findProjectionsByUsername(String username);

    /**
     * [Projections]
     * - 동적 Projections
     *  - Generic type을 주면, 동적으로 프로젝션 데이터 변경 가능
     */
    <T> List<T> findDynamicProjectionsByUsername(String username, Class<T> type);

    /**
     * [네이티브 쿼리]
     * - 페이징 지원
     * - 반환 타입(반환 타입이 유연하지 못해 사용성이 떨어진다, 하지만 이번에 Projections를 지원하면서 유연해짐, 아래에 예시 참고)
     *  - Object
     *  - Tuple
     *  - DTO
     *
     * - 제약
     *  - Sort 파라미터를 통한 정렬이 정상 동작하지 않을 수 있음(믿지 말고 직접 처리)
     *  - JPQL처럼 애플리케이션 로딩 시점에 문법 확인 불가
     *  - 동적 쿼리 불가
     *
     *  !!네이티브 SQL을 DTO로 조회할 땐 JdbcTemplate or myBatis 권장!!
     */
    @Query(value = "select * from member where username = ?", nativeQuery = true)
    Member findByNativeQuery(String username);

    /**
     * [네이티브 쿼리 Projections 활용]
     * - 정적쿼리의 경우 쓸만하다
     */
    @Query(value = "select m.member_id as id, m.username, t.name as teamName " +
    "from member m left join team t",
    countQuery = "select count(*) from member", nativeQuery = true)
    Page<MemberProjection> findByNativeProjection(Pageable pageable);
}


