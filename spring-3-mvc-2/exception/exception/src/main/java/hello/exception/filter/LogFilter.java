package hello.exception.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        String uuid = UUID.randomUUID().toString();

        try {
            // 요청 시작 (식별자, 디스패쳐타입, 요청URI)
            log.info("REQUEST [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
            // 로직 시작
            chain.doFilter(request, response);
        } catch (Exception e) {
            // 예외 발생
            log.info("EXCEPTION! {}", e.getMessage());
            throw e;
        } finally {
            // 요청 종료 (식별자, 디스패쳐타입, 요청URI)
            log.info("RESPONSE [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
