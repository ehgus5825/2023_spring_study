package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.Map;

public class MemberSaveControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        // 입력 (request가 아닌 paramMap에서 파라미터를 꺼내서 사용)
        String username = paramMap.get("username");
        int age = Integer.parseInt(paramMap.get("age"));

        // 비즈니스 로직
        Member member = new Member(username, age);
        memberRepository.save(member);

        // Model에 데이터를 보관 (뷰의 논리 이름으로 ModelView를 생성, 모델에 값을 담음)
        ModelView mv = new ModelView("save");
        mv.getModel().put("member", member);

        // 3. ModelView 반환
        return mv;

    }
}
