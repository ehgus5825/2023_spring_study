package hello.core.discount;

import hello.core.annotation.MainDiscountPolicy;
import hello.core.member.Grade;
import hello.core.member.Member;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
// @Qualifier("mainDiscountPolicy")
// @MainDiscountPolicy // 새로만든 @Qualifier
@Primary
public class RateDiscountPolicy implements DiscountPolicy {

    // 고정 할인율
    private int discountPercent = 10;

    // 할인 금액 계산
    @Override
    public int discount(Member member, int price) {
        // 회원의 등급이 VIP라면 원래의 가격에서 고정할인율만큼을 할인금액으로 책정
        if(member.getGrade() == Grade.VIP){
            return  price * discountPercent / 100;
        } else{
            return 0;
        }
    }
}
