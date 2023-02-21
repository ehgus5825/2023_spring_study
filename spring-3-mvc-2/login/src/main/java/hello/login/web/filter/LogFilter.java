package hello.login.web.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    // 필터 초기화 메서드, 서블릿 컨텍스트가 생성될 때 호출된다.
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    /**
     * 필터 메서드 :
     * - 고객의 요청이 올 때마다 메서드가 요청되므로 필터의 로직을 구현하면 된다.
     * - 요청 로직이 수행되기 전,후에 필터 로직을 수행시킬 수 있다.
     * - ServletRequest, ServletResponse는 HTTP 요청이 아닐 경우까지 고려
     * - chain.doFilter(request, response) : 다음 필터가 있으면 필터를 호출하고, 필터가 없으면 서블릿을 호출,
     *                                       해당 로직이 없으면 다음 단계 진행 X
     */

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("log filter doFilter");

        // 요청 URL을 획득
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();

        // 식별자를 생성
        String uuid = UUID.randomUUID().toString();

        try{
            // 요청 수행 시작 로그
            log.info("REQUEST [{}][{}]", uuid, requestURI);
            // 요청 수행
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            // 요청 수행 종료 로그
            log.info("RESPONSE [{}][{}]", uuid, requestURI);
        }
    }

    // 필터 종료 메서드, 서블릿 컨테이너가 종료될 때 호출된다.
    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
