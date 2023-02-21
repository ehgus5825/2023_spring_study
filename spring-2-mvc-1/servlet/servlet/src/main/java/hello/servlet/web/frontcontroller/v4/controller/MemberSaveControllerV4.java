package hello.servlet.web.frontcontroller.v4.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.v4.ControllerV4;

import java.util.Map;

public class MemberSaveControllerV4 implements ControllerV4 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public String process(Map<String, String> paramMap, Map<String, Object> model) {
        // 입력 (request가 아닌 paramMap에서 파라미터를 꺼내서 사용)
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        // 비즈니스 로직
        Member member = new Member(username, age);
        memberRepository.save(member);

        // Model에 데이터를 보관
        model.put("member", member);

        // 3. viewName 반환 (String 반환)
        return "save";
    }
}
