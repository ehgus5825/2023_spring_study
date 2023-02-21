package hello.login.domain.login;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service                    // 스프링 빈 등록
@RequiredArgsConstructor    // 생성자 주입 (의존성)
public class LoginService {

    // LoginService -> memberRepository

    private final MemberRepository memberRepository;

    /**
     * @return null 로그인 실패
     */
    public Member login(String loginId, String password) {

        // 로그인 ID가 같은 회원이 password 까지 같은지 확인해서 해당 멤버를 반환 없으면 null 반환
        // Optional이기 때문에 꺼내줘야함.
        return memberRepository.findByLoginId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
        
        /*
        Optional<Member> findMemberOptional = memberRepository.findByLoginId(loginId);
        Member member = findMemberOptional.get();
        if(member.getPassword().equals(password)){
            return member;
        } else {
            return null;
        }
        */
    }
}
