package hello.hellospring.service;

import hello.hellospring.domain.Member;
import hello.hellospring.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// service는 비즈니스와 연관성있고 확 알아볼 수 있도록 메소드명을 적어야함

// @Service // @Component
@Transactional // jpa를 사용할 때는 필수
public class MemberService {
    private final MemberRepository memberReposiitory;

    // @Autowired //MemberService -> MemberRepository (의존)
    public MemberService(MemberRepository memberRepository) {
        this.memberReposiitory = memberRepository;
    }

    // 회원 가입
    public Long join(Member member) {
        // 같은 이름이 있는 중복 회원 X
        validateDuplicateMember(member); // 중복 회원 검증

        // 회원을 받아서 중복 회원 검증 후 회원 등록
        
        memberReposiitory.save(member);
        // 회원 등록 후 등록된 회원의 id를 반환
        return member.getId();
    }

    // 중복 회원 검증
    private void validateDuplicateMember(Member member) {
        // 회원의 이름으로 전체 회원 정보에서 동일한게 있는 지 확인
        // 동일한 게 있다면 IllegalStateException 예외 발생
        memberReposiitory.findByName(member.getName())
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 회원입니다.");
                });
    }

    // 전체 회원 조회
    public List<Member> findMembers() {
        // 모든 회원 조회 메소드를 호출하여 반환
         return memberReposiitory.findAll();
    }

    // 특정 회원 조회 
    public Optional<Member> findOne(Long memberId){
        // 전달 받은 id로 회원 조회 메소드를 호출하여 값 반환
        return memberReposiitory.findById(memberId);
    }
}
