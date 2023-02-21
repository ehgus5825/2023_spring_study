package hello.login.web.member;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor        // 생성자 주입 (의존성)
@RequestMapping("/members")
public class MemberController {

    // MemberController -> MemberRepository

    private final MemberRepository memberRepository;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("member") Member member) {
        return "members/addMemberForm";
    }

    @PostMapping("/add")
    public String save(@Valid @ModelAttribute Member member, BindingResult bindingResult) {
        // 에러 발생시 다시 회원등록 폼으로
        if(bindingResult.hasErrors()) {
            return "members/addMemberForm";
        }

        // 등록 성공시 home으로
        memberRepository.save(member);
        return "redirect:/";        // home으로 가기 (HomeController)
    }
}
