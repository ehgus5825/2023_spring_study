package hello.core.member;

import hello.core.AppConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MemberServiceTest {
    // 통합 테스트엔 스프링 컨테이너 사용
    // 단위 테스트엔 자바코드로만 사용 (AppConfig 사용)
    
    
    // 이전방법
    // MemberService memberService = new MemberServiceImpl();

    // 의존성 주입
    MemberService memberService;

    @BeforeEach
    public void beforeEach(){
        // 각 테스트가 실행될 때마다 AppConfig에서 memberService를 받아옴
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
    }

    // 테스트를 사용하면 성공시 성공 결과만, 실패시 기댓값과 실제값을 한눈에 보여줘서 쉽게 문제를 해결할 수 있음
    // 테스트를 잘 작성하는 것이 정말 중요!
    @Test
    void join(){
        // given : 회원이 주어지고
        Member member = new Member(1L, "memberA", Grade.VIP);

        // when : 회원가입을 한 후
        memberService.join(member);
        Member findMember = memberService.findMember(1L);

        // then : 회원가입이 잘되었는지 확인
        Assertions.assertThat(member).isEqualTo(findMember);
    }
}
