package hello.core.order;

import hello.core.discount.FixDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import hello.core.member.MemoryMemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    @Test
    void createOrder() {
        MemoryMemberRepository memberRepository = new MemoryMemberRepository();
        memberRepository.save(new Member(1L, "name", Grade.VIP));

        // 자바 코드로만 하는 테스트일때

        // 생성자 주입시
        OrderServiceImpl orderService = new OrderServiceImpl(memberRepository, new FixDiscountPolicy());
        // 생성자 주입시 인자를 넣지 않으면 컴파일 에러가 발생 => 제일 좋은 에러는 컴파일 에러 => 생성자 주입을 권장
        // 그리고 생성자 주입시 final로 필드를 만들 수 있음. 따라서 생성자에 혹여라도 값이 설정되지 않은 경우 컴파일 시점에서 막아줌

        // 수정자 주입시
        // OrderServiceImpl orderService = new OrderServiceImpl();
        // orderService.setDiscountPolicy(memberRepository);              => 누락시 nullPointException 에러 발생 (런타임에러)
        // orderService.setMemberRepository(new FixDiscountPolicy());     => 누락시 nullPointException 에러 발생 (런타임에러)
        
        Order order = orderService.createOrder(1L, "itemA", 10000);
        assertThat(order.getDiscountPrice()).isEqualTo(1000);
    }

}