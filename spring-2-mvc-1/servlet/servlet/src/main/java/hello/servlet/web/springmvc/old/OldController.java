package hello.servlet.web.springmvc.old;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

@Component("/springmvc/old-controller")     // 빈의 이름으로 URL 매핑
public class OldController implements Controller {

    @Override // controller의 process..
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("OldController.handleRequest");
        return new ModelAndView("new-form");
    }

    // - 2가지가 필요
    // 핸들러 매핑 : 스프링 빈의 이름으로 핸들러를 찾을 수 있는 핸들러 매핑      => BeanNameUrlHandlerMapping
    // 핸들러 어댑터 : Controller 인터페이스를 실행할 수 있는 핸들러 어댑터     => SimpleControllerHandlerAdapter
}
