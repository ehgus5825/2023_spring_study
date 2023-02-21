package hello.core;

import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.order.Order;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

// JUnit을 사용하기 전 일반적인 테스트 방법
public class OrderApp {

    public static void main(String[] args) {
        // 이전방법
        // MemberService memberService = new MemberServiceImpl();
        // OrderService orderService = new OrderServiceImpl();

        // 의존성 주입
        // AppConfig appConfig = new AppConfig();
        // MemberService memberService = appConfig.memberService(); // 주문을 하기위해서는 회원이 필요하기 때문에 생성
        // OrderService orderService = appConfig.orderService();

        // 스프링 버전
        ApplicationContext ac = new AnnotationConfigApplicationContext(AppConfig.class);
        MemberService memberService = ac.getBean("memberService", MemberService.class);
        OrderService orderService = ac.getBean("orderService", OrderService.class);

        // 회원 생성 및 회원가입
        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        // 주문 생성
        Order order = orderService.createOrder(memberId, "itemA", 20000);

        // 눈으로 검증해야함
        
        // 주문확인, 실거래가격 확인
        System.out.println("order = " + order);
        System.out.println("order.calculatePrice() = " + order.calculatePrice());
    }
}
