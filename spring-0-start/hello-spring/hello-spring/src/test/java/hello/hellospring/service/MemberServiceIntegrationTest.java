package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest // 통합테스트할 수 있게 스프링 컨테이너와 테스트를 함께 실행함
@Transactional  // 테스트를 실행할 때 트랜잭션을 실행하고 롤백을 해줌 / 데이터가 디비에 반영되지 않음
                // afterEach와 beforeEach로 처리해 줄 필요가 없음
public class MemberServiceIntegrationTest {
    
    // 통합테스트이기 때문에 임시방편으로 스프링 컨테이너에 올림
    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;

    @Test // 테스트의 메소드 명을 한글로 변경해도 됨 / 빌드될때 테스트 코드는 실제 코드에 포함되지 X
    void 회원가입() {
        // save + findOne + findMember
        // given : 무언가가 주어졌는데 // 회원 객체가 주어졌고
        Member member = new Member();
        member.setName("hello");

        // when : 이걸 실행했을 때 // 회원가입을 실행하면
        Long saveId = memberService.join(member);

        // then : 이러한 결과가 나와야해  // 회원가입 후 반환된 id로 조회한 회원 객체의 이름과 등록한 회원 객체의 이름이 같아야한다.
        Member findMember = memberService.findOne(saveId).get();
        assertThat(member.getName()).isEqualTo(findMember.getName());
    }

    @Test
    public void 중복_회원_예외(){
        // given : 동일한 이름으로 두 회원이 생성되었고
        Member member1 = new Member();
        member1.setName("spring");

        Member member2 = new Member();
        member2.setName("spring");

        // when :  두 회원을 회원가입 시켰을 때
        memberService.join(member1);

        // then : IllegalStateException 에러가 발생해야하며 에러 메시지는 "이미 존재하는 회원입니다." 이어야 한다.

        // 방법 1
        /*
        try{
            memberService.join(member2);
            fail();
        } catch (IllegalStateException e){
            assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
        }
        */

        // 방법 2
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
}
