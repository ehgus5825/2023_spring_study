package hello.core.order;

import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@ToString
public class Order {

    // 회원 id
    private Long memberId;
    // 상품명
    private String itemName;
    // 상품가격
    private int itemPrice;
    // 할인 금액
    private int discountPrice;

    // 실거래가격
    public int calculatePrice(){
        return itemPrice - discountPrice;
    }
}
