package hello.login.web.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    // 컨트롤러 호출 전에 호출 ( 요청 로직 수행 전에 필터 로직을 수행할 수 있음)
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 요청 URL을 획득 => HTTP로 다운케스팅 하지 않아도 된다.
        String requestURI = request.getRequestURI();

        // 식별자를 생성
        String uuid = UUID.randomUUID().toString();
        
        // request에 식별자를 담음 (request는 요청 종료시 까지 이어져있음)
        request.setAttribute(LOG_ID, uuid);

        // @RequestMapping : HandlerMethod
        // 정적 리소스 : ResourceHttpRequestHandler
        if(handler instanceof HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;// 호출할 컨트롤러 메서드의 모든 정보가 포함되어 있다.
        }

        // 요청 수행 시작 로그 (식별자, 요청 URL, 핸들러)
        log.info("REQUEST [{}][{}][{}]", uuid, requestURI, handler);

        // => 해당 란에서 필터 로직 수행 조건 실행 후 요청 수행 여부를 체크

        // 요청 수행 (false라면 요청 수행 X)
        return true;
    }

    // 컨트롤러 호출 이후 호출
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 핸들러 어댑터 호출 후 생성된 modelAndView에 공통적인 처리를 해줄 수 있음
        
        // modelAndView를 로그로 출력
        log.info("postHandle [{}]", modelAndView);
    }

    // 뷰가 렌더링 된 이후 호출 (항상 호출됨)
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        
        // 요청 URL을 획득
        String requestURI = request.getRequestURI();

        // request의 식별자를 꺼냄 (request는 요청 종료시 까지 이어져있음)
        String uuid = (String) request.getAttribute(LOG_ID);
        
        // 요청 수행 종료 로그 (식별자, 요청 URL, 핸들러)
        log.info("RESPONSE [{}][{}][{}]", uuid, requestURI, handler);
        
        // 요청에 의해 던져진 모든 예외를 여기서 처리할 수 있음 (항상 출력되기 때문에 예외를 끝까지 받을 수 있음)
        if(ex != null){
            // 예외가 있다면 로그로 출력
            log.error("afterCompletion error!!", ex);
        }
    }
}
