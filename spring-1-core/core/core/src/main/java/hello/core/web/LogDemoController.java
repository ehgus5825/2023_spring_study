package hello.core.web;

import hello.core.common.MyLogger;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class LogDemoController {

    // 의존관계 주입
    private final LogDemoService logDemoService;
    // private final MyLogger myLogger;  
    // request 스코프임 => 고객이 들어오고 나갈때까지,, 하지만 스프링을 띄울 때는 httpRequest가 들어오지 않은 상태임
    
    // private final ObjectProvider<MyLogger> myLoggerProvider;

    private final MyLogger myLogger; // 프록시 사용 => 기존 코드 복원
    
    // localhost:8080/log-demo 를 찾음
    @RequestMapping("log-demo")
    @ResponseBody   // API 방식
    public String logDemo(HttpServletRequest request){
        String requestURL = request.getRequestURL().toString(); // URL 요청

        System.out.println("myLogger = " + myLogger); // @CGLIB ~~

        // MyLogger myLogger = myLoggerProvider.getObject();       // 이때 생성 / 원래는 의존관계 주입이 아닌 HttpRequest가 들어왔을 때 생성
        myLogger.setRequestURL(requestURL);        // myLogger에 URL 설정 / 스프링을 띄울 떄는 URL을 알수가 없다.

        // Provider을 통해서 원하는 때에 생성을 가능하게 함
        
        myLogger.log("controller test");
        logDemoService.logic("testId");
        return "OK";
    }
}
