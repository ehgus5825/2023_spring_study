package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;

import java.util.Map;

public interface ControllerV3 {

    // V3 : 해당 컨트롤러를 인터페이스로 하여 다형성 사용 + 뷰의 논리이름과 모델을 담아 ModelView를 생성하고 반환
    ModelView process(Map<String, String> paramMap);
}
