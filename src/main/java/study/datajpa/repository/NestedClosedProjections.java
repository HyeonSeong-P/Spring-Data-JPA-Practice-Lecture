package study.datajpa.repository;

/**
 * [중첩 구조 처리 Projection]
 * - 프로젝션 대상이 root 엔티티면, JPQL SELECT절 최적화 가능 (여기선 username)
 * - 프로젝션 대상이 root가 아니면 (여기선 team)
 *  - LEFT OUTER JOIN 처리
 *  - 모든 필드를 SELECT해서 엔티티로 조회한 다음에 계산
 */
public interface NestedClosedProjections {
    String getUsername();
    TeamInfo getTeam();

    interface TeamInfo{
        String getName();
    }
}
