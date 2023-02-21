package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
public class LoginCheckFilter implements Filter {   // Filter 인터페이스 구현

    /*
        HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 컨트롤러                   //로그인 사용자
        HTTP 요청 -> WAS -> 필터(적절하지 않은 요청이라 판단, 서블릿 호출X)  //비로그인 사용자
    */

    // whitelist : 인증과 무관하게 항상 허용
    private static final String[] whitelist = {"/", "/members/add", "/login", "/logout", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // HTTP로 다운 케스팅
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 요청 URL을 획득
        String requestURI = httpRequest.getRequestURI();

        try {
            log.info("인증 체크 필터 시작 {}",requestURI);

            // URL 체크 : 화이트 리스트가 아닌 URL만 if문 실행
            if(isLoginCheckPath(requestURI)){
                log.info("인증 체크 로직 실행 {}", requestURI);
                
                // 로그인을 체크하기 위한 세션을 불러옴 (없으면 null)
                HttpSession session = httpRequest.getSession(false);
                
                // 세션이 없거나 세션에서 조회한 회원정보가 없다면 if문 실행  
                if (session == null || session.getAttribute(SessionConst.LOGIN_MAMBER) == null){
                    log.info("미인증 사용자 요청 {}", requestURI);

                    // 로그인으로 redirect + 요청했던 URL을 파라미터로 가져감. 
                    // => 그 이유는 사용자가 이후 로그인을 한다면 요청했던 URL로 바로 쏴주기 위함
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);

                    // 이후 일반 로직을 실행하지 않고 종료 (서블릿 호출 X)
                    return;
                }
                
                // 세션이 있고 조회한 정보가 있다면 일반로직 실행
            }

            // 일반 로직 실행 : 화이트 리스트이거나 인증 체크 로직을 넘긴 요청만 수행  
            chain.doFilter(request, response);
        } catch (Exception e){
            throw e; // 예외 로깅 가능하지만, 톰캣까지 예외를 보내주어야 함
        } finally {
            log.info("인증 체크 필터 종료 {}", requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증 체크 X
     */
    private boolean isLoginCheckPath(String requestURI){
        // URL이 화이트 리스트에 속하면 false를 반환함
        return !PatternMatchUtils.simpleMatch(whitelist, requestURI);
    }
}
