package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// JUnit을 사용하기 전 일반적인 테스트 방법
public class MemberApp {

    public static void main(String[] args) {
        // 이전 방법
        // MemberService memberService = new MemberServiceImpl();

        // 의존성 주입
        // AppConfig appConfig = new AppConfig();
        // MemberService memberService = appConfig.memberService();

        // 스프링 버전
        // ApplicationContext를 스프링 컨테이너라고 한다.

        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);

        // 이전에는 개발자가 필요한 객체를 AppConfig를 사용해서 직접 조회했지만, 이제부터는 스프링 컨테이너를 통해서
        // 필요한 스프링 빈(객체)를 찾아야 한다. 스프링 빈은 applicationContext.getBean() 메서드를 사용해서 찾을 수 있다.
        MemberService memberService = ac.getBean("memberService", MemberService.class);

        // 회원 생성 및 회원가입
        Member member = new Member(1L, "member.A", Grade.VIP);
        memberService.join(member);

        // 눈으로 검증해야함

        // 회원 찾기
        Member findMember = memberService.findMember(1L);
        System.out.println("new member = " + member.getName());
        System.out.println("find Member = " + findMember.getName());

    }
}
