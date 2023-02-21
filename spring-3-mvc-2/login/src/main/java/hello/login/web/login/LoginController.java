package hello.login.web.login;

import hello.login.domain.login.LoginService;
import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import hello.login.web.session.SessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.http.HttpResponse;

@Slf4j
@Controller                     // 스프링 빈 등록
@RequiredArgsConstructor        // 생성자 주입 (의존성)
public class LoginController {

    // LoginController -> LoginService
    // LoginController -> SessionManager

    private final LoginService loginService;

    // 세션 관리자 생성 (세션 생성)
    private final SessionManager sessionManager;

    // ------ 로그인 ----------------------------------------------------------------------------------------------------

    @GetMapping("/login")
    public String loginForm(@ModelAttribute("loginForm") LoginForm form){
        return "login/loginForm";
    }

    /**
     * 로그인 V1 : 쿠키를 사용해서 로그인
     *
     *          - 웹브라우저가 기능을 수행할 때마다 쿠키를 보냄
     *          - 심각한 보안 이슈가 있음
     *              - 쿠키 값은 임의로 변경할 수 있다
     *              - 쿠키에 보관된 정보는 훔쳐갈 수 있다.
     *              - 해커가 쿠키를 한번 훔쳐가면 평생 사용할 수 있다.
     * 
     * @Valid : LoginForm의 @NotEmpty에 대한 검증
     * @param response : 쿠키 저장에 사용
     */

    //@PostMapping("/login")
    public String loginFormV1(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response){

        // 에러가 있다면 로그인 폼으로 돌려보냄
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 확인 후 결과가 없다면 검증 오류(글로벌 오류)를 발현시킴
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리

        // 쿠키에 시간 정보를 주지 않으면 세션 쿠키(브라우저 종료시 모두 종료)
        // 쿠키의 값을 Member 클래스의 id로 적용 후, response에 쿠키 추가
        Cookie idCookie = new Cookie("memberId", String.valueOf(loginMember.getId()));
        response.addCookie(idCookie);

        // 로그인 성공하면 home으로
        return "redirect:/";
    }

    /**
     * 로그인 V2 : 직접 만든 세션을 사용해서 로그인
     *
     *          - 복잡한 세션 ID(토큰)를 사용하고 서버에서 토큰과 사용자 ID를 매핑한다. 그리고 세션의 만료시간을 짧게 유지한다.
     *              - 쿠키 값은 임의로 변경할 수 있다                => 복잡한 세션 ID 사용
     *              - 쿠키에 보관된 정보는 훔쳐갈 수 있다.            => 세션 ID가 털려도 여기에는 중요한 정보가 없음
     *              - 해커가 쿠키를 한번 훔쳐가면 평생 사용할 수 있다.  => 세션의 만료시간을 짧게 유지한다. (이후 서블릿 세션에서 사용)
     *
     *          - 로그인 => 세션에 데이터 등록
     *
     * @Valid : LoginForm에 @NotEmpty에 대한 검증
     * @param response : 세션 생성과 세션 ID를 쿠키에 저장할 때 사용
     */

    //@PostMapping("/login")
    public String loginFormV2(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletResponse response){

        // 에러가 있다면 로그인 폼으로
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 확인 후 결과가 없다면 검증 오류(글로벌 오류)를 발현시킴
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리

        // 세션 관리자를 통해 세션에 회원 데이터 보관
        sessionManager.createSession(loginMember, response);

        // 로그인 성공하면 home으로
        return "redirect:/";
    }

    /**
     * 로그인 V3 : 서블릿 HTTP 세션을 사용해서 로그인
     *
     *          - 서블릿에서는 이미 HttpSession 이라는 기능을 제공
     *          - sessionManager과 같은 방식으로 동작
     *          - JSESSIONID 라는 쿠키를 생성 (추정 불가능)
     *
     *          - request.getSession(true or false) : 기존 세션을 불러오는 데 만약 세션이 없다면  / (create 옵션)
     *              - true => 새 세션 생성해서 반환
     *              - false => null을 반환
     *
     *          - session.setAttribute(sessionKey, value) : 세션에 키와 값을 저장
     *              - 쿠키는 알아서 생성해줌
     *
     *          - 세션 타임아웃은 마지막 행위 기준 30분이다. 30분 동안 동작이 없으면 사용을 중단한 것으로 가정하여 세션을 만료시킨다.
     *          - 타임아웃 시간을 조절하기 위해서는 "application.properties"에 server.servlet.session.timeout=1800 값을 변경해주면 된다.
     *
     * @Valid는 LoginForm에 @NotEmpty에 대한 검증
     * @param request : 서블릿 세션을 불러오는데 사용
     */

    //@PostMapping("/login")
    public String loginFormV3(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult, HttpServletRequest request){

        // 에러가 있다면 로그인 폼으로
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 확인 후 결과가 없다면 검증 오류(글로벌 오류)를 발현시킴
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리
        
        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성 (기본값 true)
        HttpSession session = request.getSession();
        // 세션 생성 후 세션에 회원 정보를 보관
        session.setAttribute(SessionConst.LOGIN_MAMBER, loginMember);

        // 로그인 성공하면 home으로
        return "redirect:/";
    }

    /**
     * 로그인 V4 : + 인증 체크 필터에서 넘어온 로그인 요청도 포함 (로그인 된다면 이전 URL 서비스를 바로 적용하기 위함)
     *
     * @Valid : LoginForm에 @NotEmpty에 대한 검증
     * @RequestParam : 인증 체크 필터에서 통과되지 못한 기존 요청의 URL을 파라미터로 받음 (기본값 : "/")
     */

    @PostMapping("/login")
    public String loginFormV4(@Valid @ModelAttribute LoginForm form, BindingResult bindingResult,
                              @RequestParam(defaultValue = "/") String redirectURL,
                              HttpServletRequest request){

        // 에러가 있다면 로그인 폼으로
        if(bindingResult.hasErrors()) {
            return "login/loginForm";
        }

        // 로그인 확인 후 결과가 없다면 검증 오류(글로벌 오류)를 발현시킴
        Member loginMember = loginService.login(form.getLoginId(), form.getPassword());
        if(loginMember == null){
            bindingResult.reject("loginFail", "아이디 또는 비밀번호가 맞지 않습니다.");
            return "login/loginForm";
        }

        // 로그인 성공 처리

        // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성 (기본값 true)
        HttpSession session = request.getSession();
        // 세션 생성 후 세션에 회원 정보를 보관
        session.setAttribute(SessionConst.LOGIN_MAMBER, loginMember);

        // 로그인 성공시
        // => 일반 로그인으로 넘어왔다면      => home으로 ("/")
        // => 인증 체크 필터에서 넘어왔다면   => 기존 요청 URL으로 ("redirectURL")
        return "redirect:" + redirectURL;
    }

    // ------ 로그아웃 --------------------------------------------------------------------------------------------------

    /**
     * 로그아웃 V1 : 쿠키를 사용해서 로그아웃
     *
     *           - expireCookie를 함수로 만들어서 사용 (빈쿠키, 유효기간 0 설정 후 등록)
     *
     * @param response : 만료된 쿠키로 쿠키를 덮어 씌울때 사용 (쿠키 만료)
     */

    //@PostMapping("/logout")
    public String logoutV1(HttpServletResponse response){

        // 쿠키 만료
        expireCookie(response, "memberId");

        // 로그아웃 성공시 home으로
        return "redirect:/";
    }

    private void expireCookie(HttpServletResponse response, String CookieName) {
        Cookie cookie = new Cookie(CookieName, null);   // 쿠키 값을 없애고
        cookie.setMaxAge(0);                                  // 유효기간을 0으로 만들어  
        response.addCookie(cookie);                           // 등록
    }
    
    /**
     * 로그아웃 V2 : 직접 만든 세션을 사용해서 로그인
     *
     *           - 로그아웃 => 세션에 데이터 삭제 (만료)
     *
     * @param request : 쿠키를 조회해서 세션을 만료하기 위해 사용
     */

    //@PostMapping("/logout")
    public String logoutV2(HttpServletRequest request) {

        // 세션에서 데이터 삭제 (만료)
        sessionManager.expire(request);

        // 로그아웃 성공시 home으로
        return "redirect:/";
    }

    /**
     * 로그아웃 V3 : 서블릿 HTTP 세션을 사용해서 로그아웃
     *
     *           - session.invalidate() : 세션을 제거한다.
     *
     * @param request : 서블릿 세션을 불러오기 위함
     */

    @PostMapping("/logout")
    public String logoutV3(HttpServletRequest request) {

        // 세션을 불러왔는데 (false라서 새로 세션을 불러오지 않음)
        HttpSession session = request.getSession(false);
        // 세션이 null이 아니라면 (세션이 있다면)
        if(session != null){
            // 세션을 만료시킴 (세션 제거)
            session.invalidate();
        }

        // 로그아웃 성공시 home으로
        return "redirect:/";
    }
}
