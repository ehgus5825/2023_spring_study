package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v4.ControllerV4;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 핸들러 어댑터
public class ControllerV4HandlerAdapter implements MyHandlerAdapter {

    // 핸들러가 MemberFormControllerV4, MemberSaveControllerV4, MemberListControllerV4 이중 하나라면
    // 해당 어댑터를 사용하도록 참을 반환
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV4);
    }

    // 어댑터에서 핸들러를 받아서 핸들러(컨트롤러)의 로직 실행
    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        // 핸들러가 Object이기 때문에 형변환 / 컨트롤러를 선택
        ControllerV4 controller = (ControllerV4) handler; // ex) MemberFormControllerV4

        // ----------------------------
        // 컨트롤러를 호출하기 전에 이 부분에서 공통로직과 같은 것을 처리할 수 있음
        // ----------------------------

        // createParamMap(request) => paramMap 생성
        Map<String, String> paramMap = createParamMap(request);
        // model을 생성
        Map<String, Object> model = new HashMap<>();

        // 4. handler 호출
        // 컨트롤러 호출, viewName 반환 (뷰의 논리 이름) / paramMap과 model을 인자로 함 (request, response => X)
        String viewName = controller.process(paramMap, model);

        // ModelView를 반환해야하기 때문에 ModelView를 생성하고 모델을 설정하는 과정이 필요
        ModelView mv = new ModelView(viewName);
        mv.setModel(model);

        // 5. ModelView 반환
        return mv;
    }

    // request의 모든 파라미터 정보를 다 복사해서 paramMap 반환.
    private static Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String,String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
