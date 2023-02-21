package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

public class MemoryMemberRepositoryTest {

    // 테스트 주도 개발 (TDD) : 테스트 틀을 만든 다음에 테스트에 맞게 서비스를 구현하는 개발법
    // 전체 테스트를 돌려보고 싶다면 상위 폴더에서 테스트를 돌리면 됨
    // 소스 코드가 엄청 거대해지면 테스트 코드 없이 개발히는 것은 불가능함. 따라서 테스트 관련해서는 깊이 있게 공부하기를 권장 

    MemoryMemberRepository repository = new MemoryMemberRepository();

    // AfterEach : 각각의 테스트가 종료될 때 마다 해당 메소드를 호출
    @AfterEach
    public void afterEach(){
        // 각각의 테스트에 대해서는 의존관계가 형성되지 않아야 하기 때문에
        // 하나의 테스트를 끝내면 메모리를 비어줘야함
        repository.clearStore();
    }

    // 회원등록 + id로 회원 찾기 테스트
    @Test
    public void save(){
        Member member = new Member();
        member.setName("spring");
        repository.save(member);

        // 한 회원을 등록한 다음 등록했던 회원의 id로 회원을 조회함.
        // get()을 사용해서 Optional을 벗겨내고 Member 타입의 변수에 값을 넣음

        Member result = repository.findById(member.getId()).get();
        System.out.println("(result == member) = " + (result == member));

        // assertEquals : 기대하는 값과 실제 값이 같은지 테스트
        assertEquals(member, result);         // junit
        // assertThat()에 넣은 값을 뒤의 isEqualTo()에 넣은 값과 같은지 비교를한다.
        assertThat(member).isEqualTo(result); // assertj
    }

    // 이름으로 회원 조회 테스트
    @Test
    public void findByName(){
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        // 두 회원을 등록한 다음 spring1이라는 이름으로 회원을 조회함.
        // get()을 사용해서 Optional을 벗겨내고 Member 타입의 변수에 값을 넣음
        
        Member result = repository.findByName("spring1").get();

        // result와 member1의 값이 같으면 테스트 성공
        assertThat(result).isEqualTo(member1);
    }

    // 모든 회원 조회 테스트
    @Test
    public void findAll(){
        Member member1 = new Member();
        member1.setName("spring1");
        repository.save(member1);

        Member member2 = new Member();
        member2.setName("spring2");
        repository.save(member2);

        // 두 회원을 등록한 다음 모든 회원을 조회하여 list에 담음 
        
        List<Member> result = repository.findAll();

        // result의 크기가 2개라면 테스트 성공
        assertThat(result.size()).isEqualTo(2);
    }
}
