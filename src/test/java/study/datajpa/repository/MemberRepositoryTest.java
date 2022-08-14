package study.datajpa.repository;

import study.datajpa.dto.MemberDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(false)
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;// 스프링 데이터 JPA가 구현체를 생성
    @Autowired TeamRepository teamRepository;
    @PersistenceContext EntityManager em;

    @Test
    public void testMember(){
        Member member = new Member("memberA");
        Member savedMember = memberRepository.save(member);

        Optional<Member> byId = memberRepository.findById(savedMember.getId());
        Member findMember = byId.get(); // Optional 처리를 바로 get으로 하는 건 좋지 않으나 예제니..

        Assertions.assertThat(findMember.getId()).isEqualTo(member.getId());
        Assertions.assertThat(findMember.getUsername()).isEqualTo(member.getUsername());
        Assertions.assertThat(findMember).isEqualTo(member); // 같은 트랜잭션 안에선 영속성 컨텍스트의 동일성을 보장(같은 인스턴스를 가짐)
    }

    /**
     * 스프링 데이터 JPA가 구현체를 생성하여 구현코드를 직접 작성하지 않아도 돼 편리하다
     */
    @Test
    public void basicCRUD(){

        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);


        // 단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        Assertions.assertThat(findMember1).isEqualTo(member1);
        Assertions.assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        Assertions.assertThat(all.size()).isEqualTo(2);

        // 카운트 검증
        long count  = memberRepository.count();
        Assertions.assertThat(count).isEqualTo(2);

        // 삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deleteCount = memberRepository.count();
        Assertions.assertThat(deleteCount).isEqualTo(0);

    }

    @Test
    public void findByUsernameAndAgeGreaterThan() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("AAA", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result =
                memberRepository.findByUsernameAndAgeGreaterThan("AAA", 15);
        assertThat(result.get(0).getUsername()).isEqualTo("AAA");
        assertThat(result.get(0).getAge()).isEqualTo(20);
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void testQuery() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<Member> result =
                memberRepository.findUser("AAA", 10);
        assertThat(result.get(0)).isEqualTo(m1);
    }

    @Test
    public void findUsernameList() {
        Member m1 = new Member("AAA", 10);
        Member m2 = new Member("BBB", 20);
        memberRepository.save(m1);
        memberRepository.save(m2);
        List<String> usernameList =
                memberRepository.findUsernameList();
        for(String s: usernameList){
            System.out.println("s = " + s);
        }
    }

    @Test
    public void findMemberDto() {
        Team team = new Team("teamA");
        teamRepository.save(team);

        Member m1 = new Member("AAA", 10);
        m1.setTeam(team);
        memberRepository.save(m1);


        List<MemberDto> memberDtos =
                memberRepository.findMemberDto();
        for(MemberDto memberDto: memberDtos){
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void paging(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",10));
        memberRepository.save(new Member("member3",10));
        memberRepository.save(new Member("member4",10));
        memberRepository.save(new Member("member5",10));

        int age = 10;

        // 페이징을 위한 config 객체를 인자로 넘겨줘야 한다.
        PageRequest pageRequest = PageRequest.of(0,3, Sort.by(Sort.Direction.DESC,"username"));

        //when
        Page<Member> page = memberRepository.findByAge(age,pageRequest); // 페이징 계산을 해야 하니 count 쿼리도 나간다.
        page.map(member -> new MemberDto(member.getId(), member.getUsername(), null)); // 엔티티로 바로 반환하는 것은 매우 좋지 않다. DTO로 변환해서 반환하자!

        //then
        List<Member> content = page.getContent(); // 조회된 데이터
        long totalCount = page.getTotalElements(); // 전체 데이터 수

        assertThat(content.size()).isEqualTo(3); // 조회된 데이터 수
        assertThat(totalCount).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); // 전체 페이지 번호
        assertThat(page.isFirst()).isTrue(); // 첫번째 항목인가?
        assertThat(page.hasNext()).isTrue(); // 다음 페이지가 있는가?

    }

    @Test
    public void bulkUpdate(){
        //given
        memberRepository.save(new Member("member1",10));
        memberRepository.save(new Member("member2",19));
        memberRepository.save(new Member("member3",20));
        memberRepository.save(new Member("member4",21));
        memberRepository.save(new Member("member5",40));


        //when
        int resultCount = memberRepository.bulkAgePlus(20);
        //em.clear();

        //then
        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    public void queryHint(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        //Member findMember = memberRepository.findById(member1.getId()).get(); // 가지고 오는 순간 원본 데이터와 가져온 데이터가 둘다 존재하여 메모리를 낭비한다.

        // 사실 트래픽이 엄청 많은 상황이 아니라면 웬만하면 사용이 잘 안된다. 대부분 쿼리를 효율적으로 짜면 성능 향상이 가능하다.
        Member findMember = memberRepository.findById(member1.getId()).get(); // QueryHint를 사용하여 최적화
        findMember.setUsername("member2");

        em.flush();
    }

    @Test
    public void lock(){
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> result = memberRepository.findLockByUsername("member1");
    }

    @Test
    public void callCustom(){
        // 스프링 데이터 JPA에서 사용자 커스텀 리포지토리 구현 클래스를 찾아준다.
        List<Member> result = memberRepository.findMemberCustom();
    }

    @Test
    public void projections() throws Exception{
        //given
        Team teamA = new Team("teamA");
        em.persist(teamA);

        Member m1 = new Member("m1", 0, teamA);
        Member m2 = new Member("m2", 0, teamA);
        em.persist(m1);
        em.persist(m2);

        em.flush();
        em.clear();

        //when
        List<UsernameOnly> result = memberRepository.findProjectionsByUsername("m1"); // sqpl에서도 select절에서 username만 조회(projection)하는 것을 확인
        //List<UsernameOnly> resultDynamic = memberRepository.findDynamicProjectionsByUsername("m1",UsernameOnly.class); // 동적 projection

        //then
        for(UsernameOnly usernameOnly:result){
            System.out.println("usernameOnly = " + usernameOnly);
        }
        assertThat(result.size()).isEqualTo(1);
    }
}