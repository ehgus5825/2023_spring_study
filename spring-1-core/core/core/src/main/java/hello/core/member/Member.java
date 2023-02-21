package hello.core.member;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;


@AllArgsConstructor
@Getter
@Setter
public class Member {
    // 회원 id
    private Long id;
    // 회원 이름
    private String name;
    // 회원 등급
    private Grade grade;
}
