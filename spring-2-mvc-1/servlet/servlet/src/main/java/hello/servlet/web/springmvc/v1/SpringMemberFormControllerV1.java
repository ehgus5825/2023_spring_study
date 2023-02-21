package hello.servlet.web.springmvc.v1;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller // 자동 스프링 빈 등록 + 애노테이션 기반 컨트롤러 인식 (@Component + @RequestMapping)
public class SpringMemberFormControllerV1 {

    @RequestMapping("/springmvc/v1/members/new-form")   // 요청 정보 매핑 / 해당 URL이 호출되면 이 메서드가 호출
    public ModelAndView process(){                      // 애노테이션 기반으로 작동되기 때문에 함수명은 자유
        return new ModelAndView("new-form");   // ModelAndView : 모델과 뷰 정보를 담아서 반환
    }

    // RequestMappingHandlerMapping은 스프링 빈 중에서 @RequestMapping(스프링 3.0 이하) 또는 @Controller가 클래스 레벨에 붙어 있는 경우 매핑 정보 인식
}
