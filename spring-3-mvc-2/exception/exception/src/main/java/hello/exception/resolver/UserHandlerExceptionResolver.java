package hello.exception.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import hello.exception.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class UserHandlerExceptionResolver implements HandlerExceptionResolver {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        try {

            // 오류를 여기서 바로 처리함 => API 오류일시 서블릿 밖으로 나가져 reseponse 값 출력 / 오류 페이지 요청일 시 뷰 바로 렌더링

            if (ex instanceof UserException){
                // 500 에러를 400으로 바꿔서 setStatus
                log.info("UserException resolver to 400");
                String acceptHeader = request.getHeader("accept");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

                // 클라이언트의 Accept 값이 json이라면 오류코드를 JSO
                if ("application/json".equals(acceptHeader)){
                    // 오류 메시지를 Map에 담음
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("ex", ex.getClass());
                    errorResult.put("message", ex.getMessage());

                    // json으로 매핑
                    String result = objectMapper.writeValueAsString(errorResult);

                    response.setContentType("application/json");
                    response.setCharacterEncoding("utf-8");

                    // response에 json 값 직접 넣어줌 => 그대로 출력됨. (WAS로 감)
                    response.getWriter().write(result);

                    // 빈 ModelAndView
                    return new ModelAndView();
                }
                // 클라이언트의 Accept 값이 HTML이라면 뷰 렌더링 (바로 뷰를 보여줌)
                else {
                    // ModelAndView 지정
                    return new ModelAndView("error/4xx");
                }

            }

        } catch (IOException e) {
            log.error("resolver ex", e);
        }

        return null;
    }
}
