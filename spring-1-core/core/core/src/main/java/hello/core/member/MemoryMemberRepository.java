package hello.core.member;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MemoryMemberRepository implements MemberRepository{

    // 전체 회원을 담는 저장소 (키(id), 회원정보(Member))
    private static Map<Long, Member> store = new HashMap<>();

    // 회원 등록 (구현)
    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    // 회원 찾기 (구현)
    @Override
    public Member findById(Long memberId) {
        return store.get(memberId);
    }

}
