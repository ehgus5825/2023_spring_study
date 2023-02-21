package hello.hellospring.controller;

import hello.hellospring.domain.Member;
import hello.hellospring.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller // @Component
public class MemberController {

    // private final MemberService memberService = new MemberService();
    
    // 하나만 생성해놓고 공용으로 쓰면 됨 (싱글톤, 컴포넌트 스캔과 자동 의존관계 설정으로 스프링 컨테이너가 알아서 관리해줌)
    private final MemberService memberService;

    @Autowired // MemberController -> MemberService (의존)
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 가입 (view)
    @GetMapping(value = "/members/new")
    public String createForm() {
        return "members/createMemberForm";
    }

    // 회원 가입 (logic)
    // action의 링크, post 방식으로 값 등록시 메소드 실행 
    @PostMapping("/members/new")
    public String create(MemberForm form){
        // MemberForm의 값으로 html에서 보냈던 값들이 들어옴
        Member member = new Member();
        // 회원의 이름을 form의 값으로 설정함
        member.setName(form.getName());
        // 회원가입 실행
        memberService.join(member);
        // 뒤로 가기
        return "redirect:/";
    }

    // 회원 목록 (view)
    @GetMapping("/members")
    public String list(Model model){
        // 현재 저장되어 있는 회원을 모두 members에 받음
        List<Member> members = memberService.findMembers();
        // html에 "members"를 키로 하여 members 리스트를 보냄
        model.addAttribute("members", members);
        // members/memberList
        return "members/memberList";
    }
}
