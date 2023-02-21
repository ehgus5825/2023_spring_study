package hello.core.order;

import hello.core.AppConfig;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemberService;
import hello.core.member.MemberServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderServiceTest {

    // 이전방법
    // MemberService memberService = new MemberServiceImpl();
    // OrderService orderService = new OrderServiceImpl();

    // 의존성 주입
    MemberService memberService;
    OrderService orderService;

    @BeforeEach
    public void beforeEach(){
        // 각 테스트가 실행될 때마다 AppConfig에서 memberService와 orderService를 받아옴
        AppConfig appConfig = new AppConfig();
        memberService = appConfig.memberService();
        orderService = appConfig.orderService();
    }
    
    @Test
    void createOrder() {
        // given : 회원생성 및 회원가입 
        Long memberId = 1L;
        Member member = new Member(memberId, "memberA", Grade.VIP);
        memberService.join(member);

        // when : 회원 id와 상품명, 상품가격을 넣어서 주문 생성
        Order order = orderService.createOrder(memberId, "itemA", 10000);

        // then : 생성된 주문의 할인금액과 예상하는 할인금액이 같아야함
        Assertions.assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }

    /*
    @Test
    void fieldInjectionTest(){
        // @Autowired private MemberRepository memberRepository;
        // @Autowired private DiscountPolicy discountPolicy;
        // 위처럼 필드 주입 시 일반 테스트 (스프링 테스트 X)
        OrderServiceImpl orderService = OrderServiceImpl(new ...); // => 에러 발생
        // 생성자가 없기 때문에 인자로 의존관계를 넣어 줄 수 없음.
        orderService.createOrder(1L, "itemA", 10000);
        // 따라서 필드 주입 방식을 사용하면 테스트 하기가 힘들다.
        
        // 이럴경우 Setter을 만들어야함.. 악순환
    }
     */

}
