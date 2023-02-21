package hello.login.web.interceptor;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {      // HandlerInterceptor 인터페이스 구현

    /*
        HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러                                  //로그인 사용자
        HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 (적절하지 않은 요청이라 판단, 컨트롤러 호출 X)    //비로그인 사용자
     */

    // preHandle는 반환 값이 true라면 요청 로직 수행, false라면 요청 로직 수행하지 않고 종료.

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // HTTP로 다운 케스팅 안해도 됨

        // 요청 URL 획득
        String requestURI = request.getRequestURI();

        // Filter을 사용하면 URL 체크를 해야하지만 할 필요가 없음, 이미 제외가 되서 들어옴

        log.info("인증 체크 인터셉터 실행 {}", requestURI);

        // 로그인을 체크하기 위한 세션을 불러옴 (없으면 null)
        HttpSession session = request.getSession(false);

        // 세션이 없거나 세션에서 조회한 회원정보가 없다면 if문 실행
        if(session == null || session.getAttribute(SessionConst.LOGIN_MAMBER) == null){
            log.info("미인증 사용자 요청");

            // 로그인으로 redirect + 요청했던 URL을 파라미터로 가져감.
            // => 그 이유는 사용자가 이후 로그인을 한다면 요청했던 URL로 바로 쏴주기 위함
            response.sendRedirect("/login?redirectURL=" + requestURI);

            // 이후 일반 로직을 실행하지 않고 종료 (서블릿 호출 X)
            return false;
        }

        // 세션이 있고 조회한 정보가 있다면 일반로직 실행
        return true;
    }
}
