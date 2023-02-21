package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// 자기가 알아서 스프링에 구현체를 등록해줌
public interface SpringDataJpaMemberRepository extends JpaRepository<Member, Long>, MemberRepository {
    @Override
    Optional<Member> findByName(String name);
    // 인터페이스에 공통 메소드로 제공 불가능 -> 메소드에 이름을 유추해서 매핑하여 기능을 만들어줌

}
