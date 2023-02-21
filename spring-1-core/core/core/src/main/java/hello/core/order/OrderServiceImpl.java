package hello.core.order;

import hello.core.discount.DiscountPolicy;
import hello.core.discount.FixDiscountPolicy;
import hello.core.discount.RateDiscountPolicy;
import hello.core.member.Member;
import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component // ("service")
// @RequiredArgsConstructor // 롬복 사용시 생성자 주입 (생략 가능 @Autowired + 롬복)
public class OrderServiceImpl implements OrderService {

    // 회원 등급을 알아내기(회원찾기) 위한 memberRepository 생성 --------------------------------------------------------------
    // private final MemberRepository memberRepository = new MemoryMemberRepository();
    
    // 회원 등급에 따른 할인 금액을 알아내기 위한 FixDiscountPolicy 생성 => RateDiscountPolicy 변경 -----------------------------
    // private final DiscountPolicy discountPolicy = new FixDiscountPolicy();
    // private final DiscountPolicy discountPolicy = new RateDiscountPolicy();

    // 위의 코드는 다형성은 지키고 있지만 OCP와 DIP는 지키고 있지 않음, 위의 코드는 클라이언트가 역할과 구현 두가지 모두에게 의존하고 있음.
    // OCP와 DIP가 가능하도록 수정

    private final MemberRepository memberRepository;
    private final DiscountPolicy discountPolicy;
    // 롬복을 쓰면 의존관계 추가시 정말 편해짐

    // 변경된 위의 코드는 인터페이스에만 의존함. 하지만 nullPointException 에러 발생 따라서 누군가가 의존관계를 대신 주입해야함
    
    // 위와 같은 문제를 해결하기 위해 생성자를 만들고 생성자를 통해 외부에서 구현체(의존관계)를 주입하도록 변경하고
    // 외부(AppConfig)에 모아서 의존관계를 일괄 관리할 수 있도록 한다. (역할을 AppConfig에 위임)

    // 롬복 사용전 생성자 주입
    @Autowired
    public OrderServiceImpl(MemberRepository memberRepository, DiscountPolicy discountPolicy) {
        // 1. 필드명, 파라미터명 변경
        // 자동 주입시 조회 빈이 중복 된다면 필드명을 변경해서 해결 (discountPolicy => rateDiscountPolicy)
        // 스프링 빈 객체에서 rateDiscountPolicy을 찾음

        this.memberRepository = memberRepository;
        this.discountPolicy = discountPolicy;

        // 2. @Qualifier
        // public OrderServiceImpl(MemberRepository memberRepository, @Qualifier("mainDiscountPolicy") DiscountPolicy discountPolicy) { }
        // @Qualifier 사용시 지정된 명으로 스프링 빈을 찾는다
        // @Qualifier로 지정된 스프링 빈을 못찾으면 @Qualifier 설정된 이름으로 스프링 빈을 찾아본다.

        // 2-1. @Qualifier로 애노테이션 생성
        // public OrderServiceImpl(MemberRepository memberRepository, @MainDiscountPolicy DiscountPolicy discountPolicy) { }
        // 추적 가능
        // @Qualifier 문자이기 때문에 오타가 나면 컴파일시 오류를 잡을 수 없음
        // 애노테이션으로 정의를 해놓는 다면 오타시 컴파일 에러로 오류를 잡을 수 있음

        // 3. @Primary
        // 스프링 빈 중 @Primary가 쓰여진 스프링 빈을 선택한다.

        // 중복된 스프링 빈 중 조회할 때 우선순위는 @Qualifier(상세) > @Primary(기본값)이다.
    }

    // 생성자를 통해서 외부에서 구현체(의존관계)를 주입하기 때문에 해당 클라이언트는 구현체에 대해 전혀 몰라도 된다.
    // 따라서 사용영역(클라이언트)에서 구현체를 등록하는 역할을 없앨 수 있어 자신의 역할에만 충실할 수 있게 된다. (SRP)
    // 그리고 클라이언트는 구현체말고 인터페이스에만 의존관계를 가지고 있다. (DIP)
    // 또한 의존관계가 외부에서 주입되기 때문에 클라이언트의 수정없이 확장이 가능하다. (OCP)

    @Override
    // 주문 생성
    // 회원정보, 주문, 할인의 역할이 잘 구분되어 단일책임 원칙을 잘 지키고 잘 설계됨
    public Order createOrder(Long memberId, String itemName, int itemPrice) {
        // 회원 받아오기(회원 등급 내포)
        Member member = memberRepository.findById(memberId);
        // 회원과 상품가격을 통한 할인금액 받아오기
        int discountPrice = discountPolicy.discount(member, itemPrice);
        // 인자로 받아온 3가지의 값과 할인금액을 넣어서 주문 객체를 만든 후 반환
        return new Order(memberId, itemName, itemPrice, discountPrice);
    }

    // 테스트 용도
    public MemberRepository getMemberRepository() {
        return memberRepository;
    }
}
