package hello.servlet.domain.member;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MemberRepositoryTest {

    // 싱글톤이므로 getInstance로 memberRepository를 받아옴
    MemberRepository memberRepository = MemberRepository.getInstance();

    // 하나의 테스트가 끝나면 실행되는 메소드
    @AfterEach
    void afterEach(){
        // 저장소 초기화
        memberRepository.clearStore();
    }

    // 회원 저장 테스트
    @Test
    void save(){
        // given : 회원 생성
        Member member = new Member("hello", 20);

        // when : 회원 저장
        Member savedMember = memberRepository.save(member);

        // then : 저장된 회원의 id로 member을 찾아서 동일한지 확인
        Member findMember = memberRepository.findById(savedMember.getId());
        assertThat(findMember).isEqualTo(savedMember);
    }

    // 전체 회원 조회 테스트
    @Test
    void findAll(){
        // given : 두 회원 생성 후 저장
        Member member1 = new Member("member1", 20);
        Member member2 = new Member("member2", 30);

        memberRepository.save(member1);
        memberRepository.save(member2);

        // when : 모든 회원 조회
        List<Member> result = memberRepository.findAll();

        // then : 회원 수와 저장된 회원이 list에 포함되어 있는지 확인
        assertThat(result.size()).isEqualTo(2);
        assertThat(result).contains(member1, member2);
    }

}