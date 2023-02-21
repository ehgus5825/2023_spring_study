package hello.servlet.web.springmvc.v3;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * v3
 * Model 도입
 * ViewName 직접 반환
 * @RequestParam 사용
 * @RequestMapping -> @GetMapping, @PostMapping
 */
@Controller
@RequestMapping("springmvc/v3/members")
public class SpringMemberControllerV3 {

    private MemberRepository memberRepository = MemberRepository.getInstance();

    // @RequestMapping(value = "/new-form", method = RequestMethod.GET)
    @GetMapping("/new-form")
    public String newForm(){
        return "new-form";
    }

    // @RequestMapping(value = "/save", method = RequestMethod.POST)
    @PostMapping("/save")                   // ↑ 의 코드와 동일, HTTP 메서드에 따라 매핑되어 호출됨 / put, patch, delete 모두 지원
    public String save(
            @RequestParam("username") String username,
            @RequestParam("age") int age,   // Integer.parseInt(request.getParameter("age")); 와 거의 동일
            Model model) {                  // Model을 인자로 받아서 값을 저장함. / 반환할 필요 X, 프레임워크 만들기의 v4 버전과 상응

        Member member = new Member(username, age);
        memberRepository.save(member);

        model.addAttribute("member", member);
        return "save";                      // 뷰 논리이름 String 반환. / 프레임워크 만들기의 v4 버전과 상응
    }

    // @RequestMapping(method = RequestMethod.GET)
    @GetMapping
    public String members(Model model) {
        List<Member> members = memberRepository.findAll();

        model.addAttribute("members", members);
        return "members";
    }
}
