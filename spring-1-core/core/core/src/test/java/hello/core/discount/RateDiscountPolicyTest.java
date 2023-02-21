package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class RateDiscountPolicyTest {

    RateDiscountPolicy discountPolicy = new RateDiscountPolicy();

    @Test
    @DisplayName("VIP는 10% 할인이 적용되어야 한다.") // Test명 주입
    void vip_o(){
        //given : vip인 회원이 주어졌을때
        Member member = new Member(1L, "memberVIP", Grade.VIP);
        //when : 상품 가격이 10000원이라면
        int discount = discountPolicy.discount(member, 10000);
        //then : 10%인 1000원이 할인되어야한다.
        assertThat(discount).isEqualTo(1000);
    }

    @Test
    @DisplayName("VIP가 아니면 할인이 적용되지 않아야 한다.") // Test명 주입
    void vip_x(){
        //given : basic인 회원이 주어졌을때
        Member member = new Member(1L, "memberBASIC", Grade.BASIC);
        //when : 상품 가격이 10000원이라면 
        int discount = discountPolicy.discount(member, 10000);
        //then : 할인이 적용되지 않아야한다.
        assertThat(discount).isEqualTo(0);
    }
}