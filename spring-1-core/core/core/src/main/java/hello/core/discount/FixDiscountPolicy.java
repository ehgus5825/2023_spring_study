package hello.core.discount;

import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
// @Qualifier("fixDiscountPolicy")
public class FixDiscountPolicy implements DiscountPolicy{

    // 고정 할인 금액
    private int discountFixAmount = 1000; // 1000원 할인

    // 할인 금액 계산
    @Override
    public int discount(Member member, int price) {
        // 전달받은 회원의 등급이 VIP라면 고정할인 금액을 반환
        if(member.getGrade() == Grade.VIP) {
            return discountFixAmount;
        } else {
            return 0;
        }
    }
}
