package hello.core.member;

public interface MemberRepository {

    // 회원 등록
    void save(Member member);

    // 회원 찾기
    Member findById(Long memberId);
}
