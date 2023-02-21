package hello.core;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.MemberRepository;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import hello.core.member.MemoryMemberRepository;
import hello.core.order.OrderService;
import hello.core.order.OrderServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// AppConfig에 설정을 구성한다는 뜻의 @Configuration 을 붙여준다.
@Configuration
public class AppConfig {
    // 구성영역(AppConfig)은 모든 구현체를 다 알아야한다.

    // 스프링 컨테이너는 @Configuration이 붙은 AppConfig를 설정(구성) 정보로 사용한다. 여기서 @Bean이라 적힌 메서드를
    // 모두 호출해서 반환된 객체를 스프링 컨테이너에 등록한다. 이렇게 스프링 컨테이너에 등록된 객체를 스프링 빈이라 한다

    /**
     * 기존
    public MemberService memberService(){
        return new MemberServiceImpl(new MemoryMemberRepository());
    }

    public OrderService orderService(){
        return new OrderServiceImpl(new MemoryMemberRepository(), new RateDiscountPolicy());
    }
    **/

    // 중복제거 후 (리팩토링) 
    // 인자의 new 를 모두 메소드로 추출하여 중복을 제거
    // 역할과 구현 클래스가 한눈에 보임

    // 각 메서드에 @Bean을 붙여준다. 이렇게 하면 스프링 컨테이너에 스프링 빈으로 등록한다.

    // 스프링 빈은 @Bean이 붙은 메서드의 명을 스프링 빈의 이름으로 사용한다. ( memberService , orderService )

    // @Bean memberService -> new MemoryMemberRepository();
    // @Bean orderService -> new MemoryMemberRepository();
    // => 싱글톤이 깨지는 것이 아닌가요?

    // 예상 출력
    // call AppConfig.memberService
    // call AppConfig.memberRepository
    // call AppConfig.memberRepository
    // call AppConfig.orderService
    // call AppConfig.memberRepository

    // 실제 출력
    // call AppConfig.memberService
    // call AppConfig.memberRepository
    // call AppConfig.orderService
    
    // 스프링이 정말 어떠한 방법을 써서라도 싱글톤을 보장해주는구나

    @Bean
    public MemberService memberService(){
        System.out.println("call AppConfig.memberService");
        return new MemberServiceImpl(memberRepository());
    }

    @Bean
    public MemberRepository memberRepository() {
        System.out.println("call AppConfig.memberRepository");
        return new MemoryMemberRepository();
    }
    /**
     * AppConfig@CGLIB 예상 코드
    @Bean
    public MemberRepository memberRepository() {

        if (memoryMemberRepository가 이미 스프링 컨테이너에 등록되어 있으면?) {
            return 스프링 컨테이너에서 찾아서 반환;
        } else { //스프링 컨테이너에 없으면
            기존 로직을 호출해서 MemoryMemberRepository를 생성하고 스프링 컨테이너에 등록
            return 반환
        }
    }
    */
    
    // @Configuration을 사용하면 @Bean이 붙은 메서드마다 이미 스프링 빈이 존재하면 존재하는 빈을 반환하고, 스프링 빈이 없으면
    // 생성해서 스프링 빈으로 등록하고 반환하는 코드가 동적으로 만들어진다 => 싱글톤 보장

    // 그렇다면 @Configuration을 적용하지 않고 @Bean만 적용한다면 어떻게 될까?
    // 객체들은 스프링 컨테이너에 올라가지만 싱글톤을 보장하지 않는다. 그 이유는 위의 예상코드에 걸려저서 객체가 생성되는 것이 아니기 때문.
    // 따라서 일반 객체가 생성되고 그 객체는 싱글톤이 보장되지 않는다.
    // => 다른 인스턴스를 반환하고, 객체도 여러번 불러진다. => 싱글톤이 적용되지 않음
    // 따라서 스프링 설정 정보에는 항상 @Configuration을 꼭 적용하자.

    @Bean
    public OrderService orderService(){
        System.out.println("call AppConfig.orderService");
        return new OrderServiceImpl(memberRepository(), discountPolicy());
    }

    // 사용영역(OrderService)이 아닌 구성영역(AppConfig)의 코드를 변경해서 구현체를 바꿀 수 있다.
    @Bean
    public DiscountPolicy discountPolicy() {
        // return new FixDiscountPolicy();
        return new RateDiscountPolicy();
    }
}
