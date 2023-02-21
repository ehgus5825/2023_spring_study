package hello.servlet.domain.member;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려
 */

public class MemberRepository {

    // 저장소
    private static Map<Long, Member> store = new HashMap<>();
    // 할당되는 id
    private static long sequence = 0L;

    // ----- 싱글톤을 위한 세팅 -------
    private static final MemberRepository instance = new MemberRepository();

    public static MemberRepository getInstance(){
        return instance;
    }

    private MemberRepository(){

    }
    // -----------------------------

    // 회원 저장
    public Member save(Member member){
        // 인자로 받은 member에 id 할당
        member.setId(++sequence);
        store.put(member.getId(), member);
        return member;
    }

    // 특정 회원 조회
    public Member findById(Long id){
        return store.get(id);
    }

    // 회원 모두 조회
    public List<Member> findAll(){
        // List에 모두 담아 반환
        return new ArrayList<>(store.values());
    }

    // 저장소 초기화
    public void clearStore(){
        store.clear();
    }
}
