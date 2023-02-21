package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import hello.hellospring.repository.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

// 단위 테스트 : 자바 코드로만 하는 테스트
// 통합 테스트 : DB나 스프링 컨테이너와 함께 실행하는 테스트
// 통합 테스트가 있으니 단위 테스트는 필요가 없나? => X 순수한 단위 테스트가 훨씬 좋아야한다.
// 어쩔 수 없이 통합 테스트를 해야하는 상황이면 테스트 설계가 잘 못 되었을 수도 있다.
class MemberServiceTest {

    // MemberService memberService = new MemberService();
    // MemberRepository memberRepository = new MemoryMemberRepository();
    // memberService의 memberRepository와 MemberServiceTest의 memberRepository는 다른 객체임..
    // 지금은 static이라서 상관없지만 static이 아니라면 new로 다른 객체가 생성되기 때문에 각각의 값이 달라질 수 있음 // 같은 걸로 테스트 해야함
    // 직접 new해서 생성하는 것이 아니라 외부에서 넣어줄 수 있도록 바꿔야함. (생성자 주입)

    MemberService memberService;
    MemoryMemberRepository memberRepository;

    @BeforeEach
    public void beforeEach(){
        // 테스트를 시작할때마다 서비스의 저장소에 repository를 넣어줌 (새 객체 생성)
        memberRepository = new MemoryMemberRepository();
        memberService = new MemberService(memberRepository);
    }

    @AfterEach
    public void afterEach(){
        // 테스트가 끝날때마다 저장소에 있는 값들을 모두 삭제해줌
        memberRepository.clearStore();
    }

    @Test
    // 테스트의 메소드 명을 한글로 변경해도 됨 / 빌드될때 테스트 코드는 실제 코드에 포함되지 X
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