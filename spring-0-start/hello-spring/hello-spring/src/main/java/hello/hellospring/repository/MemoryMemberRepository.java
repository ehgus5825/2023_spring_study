package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.stereotype.Repository;

import java.util.*;

// 동시성 문제가 고려되어 있지 않음, 실무에서는 ConcurrentHashMap, AtomicLong 사용 고려
// @Repository // @Component
public class MemoryMemberRepository  implements  MemberRepository {

    // store는 id와 member가 저장됨
    private static Map<Long, Member> store = new HashMap<>();
    private static long sequence = 0L;

    // 회원 등록
    @Override
    public Member save(Member member) {
        // 회원 등록때 마다 id +1 해서 회원 id 값 설정
        member.setId(++sequence);
        // id와 회원 객체를 store에 저장
        store.put(member.getId(), member);
        // 등록한 회원을 반환
        return member;
    }

    // id로 회원 찾기
    @Override
    public Optional<Member> findById(Long id) {
        // 전달된 id를 통해서 store의 회원 정보를 반환함
        // Optional.ofNullable() 이기 때문에 null도 저장 가능
        return Optional.ofNullable(store.get(id));
    }

    // 이름으로 회원 찾기
    @Override
    public Optional<Member> findByName(String name) {
        // values() : map에서 값들만 가져와서 collection 으로 변경
        // stream() : collection을 stream으로 만듬
        // filter() : stream 내의 값들을 인자로 부여된 람다식을 통해서 걸러냄 / 회원 이름이 인자의 값과 동일한 것만 
        // findAny() : 그 중 가장 먼저 탐색되는 요소를 반환
        return store.values().stream().
                filter(member -> member.getName().equals(name))
                .findAny();
    }

    // 모든 회원 조회
    @Override
    public List<Member> findAll() {
        // map의 모든 값들을 Arraylist로 만들어서 반환
        return new ArrayList<>(store.values());
    }

    // 모든 회원 삭제
    public void clearStore(){
        // map의 모든 값을 삭제
        store.clear();
    }
}
