package hello.servlet.web.frontcontroller.v3.controller;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;

import java.util.List;
import java.util.Map;

public class MemberListControllerV3 implements ControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    @Override
    public ModelView process(Map<String, String> paramMap) {
        // 비즈니스 로직
        List<Member> members = memberRepository.findAll();

        // Model에 데이터를 보관 (뷰의 논리 이름으로 ModelView를 생성, 모델에 값을 담음)
        ModelView mv = new ModelView("members");
        mv.getModel().put("members", members);

        // 3. ModelView 반환
        return mv;
    }
}
