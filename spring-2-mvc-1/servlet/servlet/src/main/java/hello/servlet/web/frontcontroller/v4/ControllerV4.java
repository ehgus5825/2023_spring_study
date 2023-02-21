package hello.servlet.web.frontcontroller.v4;

import java.util.Map;

public interface ControllerV4 {

    // V4 : 해당 컨트롤러를 인터페이스로 하여 다형성 사용 + 추가된 인자인 model에 값을 저장하고 viewName(String)을 반환
    String process(Map<String, String> paramMap, Map<String, Object> model);
}
