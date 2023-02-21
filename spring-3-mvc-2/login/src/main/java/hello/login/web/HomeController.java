package hello.login.web;

import hello.login.domain.member.Member;
import hello.login.domain.member.MemberRepository;
import hello.login.web.argumentresolver.Login;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    // HomeController -> MemberRepository
    // HomeController -> SessionManager

    private final MemberRepository memberRepository;

    // 세션 관리자 생성 (세션 생성)
    private final SessionManager sessionManager;

    /**
     * Home : 회원가입 or 로그인 선택화면
     */

    //@GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * HomeLogin V1 : loginHome or home (쿠키를 사용)
     *
     *              - "/" 요청시 Home 화면(회원가입/로그인) or loginHome 화면(상품관리/로그아웃), 둘 중 선택하여 이동
     *
     * @CookieValue : 웹브라우저에서 보낸 name(memberId)의 쿠키를 파라미터로 바로 받을 수 있음.
     *                "required = false"는 memberId가 null도 허용하겠다는 의미
     */

    //@GetMapping("/")
    public String homeLoginV1(@CookieValue(name = "memberId", required = false) Long memberId, Model model) {

        // memberId가 null이면 웹브라우저에서 보낸 쿠키가 없다면 로그인 X => home으로
        if( memberId == null){
            return "home";
        }

        // memberId로 저장소를 조회시 값이 없다면 로그인 X => home으로
        Member loginMember = memberRepository.findById(memberId);
        if(loginMember == null){
            return "home";
        }

        // 저장소 조회 후 값이 있다면 로그인 O => 모델에 값을 담아서 loginHome으로
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /**
     * HomeLogin V2 : loginHome or home (직접 만든 세션을 사용)
     *
     *              - Home or loginHome => 세션에 데이터 조회 => 회원 정보 Not null or Null
     *
     * @param request : 세션 조회시 request의 쿠키에 담긴 세션 ID를 사용
     */

    //@GetMapping("/")
    public String homeLoginV2(HttpServletRequest request, Model model) {

        // 세션 관리자에 저장된 회원 정보 조회
        Member member = (Member) sessionManager.getSession(request);

        // 세션 관리자에 저장된 회원 정보가 없다면 로그인 X => home으로
        if(member == null){
            return "home";
        }

        // 세션 관리자에 저장된 회원 정보가 있다면 로그인 O => 모델에 값을 담아서 loginHome으로
        model.addAttribute("member", member);
        return "loginHome";
    }

    /**
     * HomeLogin V3 : loginHome or home (서블릿 HTTP 세션을 사용)
     *              
     *              - session.getAttribute(sessionkey) : sessionKey로 세션의 값을 조회 => sessionKey와 매핑된 값을 반환
     *              
     * @param request : 세션을 불러오는데 사용
     */

    //@GetMapping("/")
    public String homeLoginV3(HttpServletRequest request, Model model) {

        // 세션을 불러옴 (새로 세션을 생성하지 않음)
        HttpSession session = request.getSession(false);
        
        // 기존 세션이 없다면 로그인 X => home으로
        if(session == null) {
            return "home";
        }

        // 세션에 저장된 회원 정보를 조회
        Member loginMember = (Member) session.getAttribute(SessionConst.LOGIN_MAMBER);

        // 세션에 저장된 회원 정보가 없다면 로그인 X => home으로
        if(loginMember == null){
            return "home";
        }

        // 세션에 저장된 회원 정보가 있다면 로그인 O => 모델에 값을 담아서 loginHome으로
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /**
     * HomeLogin V3 Spring : loginHome or home (스프링의 @SessionAttribute 지원)
     *
     * @SessionAttribute : 세션 불러오기 (세션 생성 X) => name의 값을 키로하여 세션에 저장된 회원 정보를 조회해서 파라미터에 담음
     *                     => 세션 불러오기, 세션 조회, 회원정보 담기의 모든 행위를 축약한 애노테이션
     *                     => "required = false" 이기 때문에 null(세션 조회 실패)의 값을 허용함
     *                     => 파라미터에 reuqest를 넣을 필요가 없음
     */

    //@GetMapping("/")
    public String homeLoginV3Spring(
            @SessionAttribute(name = SessionConst.LOGIN_MAMBER, required = false) Member loginMember, Model model) {
        
        // 세션에 저장된 회원 정보가 없다면 로그인 X => home으로
        if(loginMember == null){
            return "home";
        }

        // 세션에 저장된 회원 정보가 있다면 로그인 O => 모델에 값을 담아서 loginHome으로
        model.addAttribute("member", loginMember);
        return "loginHome";
    }

    /**
     * HomeLogin V3 ArgumentResolver : @SessionAttribute => @Login 변경
     *
     *              - @Login 애노테이션 생성 (ArgumentResolver 커스텀)
     *              - 자동으로 세션에 있는 로그인 회원을 찾아주고, 만약 세션에 없다면 null을 반환하도록 구현
     *
     *              - @Login Member
     *                  => ArgumentResolver [ supportsParameter -> resolveArgument]
     *                      => Object (null or 세션 내의 회원 정보)
     */

    @GetMapping("/")
    public String homeLoginV3ArgumentResolver(@Login Member loginMember, Model model) {

        // 세션에 저장된 회원 정보가 없다면 로그인 X => home으로 없으면 home
        if(loginMember == null){
            return "home";
        }

        // 세션에 저장된 회원 정보가 있다면 로그인 O => 모델에 값을 담아서 loginHome으로
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}