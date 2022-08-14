package study.datajpa.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * [클래스 기반 Projection]에 필요한 DTO
 * - 생성자의 파라티머 이름으로 매핑
 */
@Getter
@RequiredArgsConstructor
public class UsernameOnlyDto {
    private final String username;
}
