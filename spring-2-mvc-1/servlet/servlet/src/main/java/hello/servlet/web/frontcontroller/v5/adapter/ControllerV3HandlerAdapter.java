package hello.servlet.web.frontcontroller.v5.adapter;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.v3.ControllerV3;
import hello.servlet.web.frontcontroller.v5.MyHandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 핸들러 어댑터
public class ControllerV3HandlerAdapter implements MyHandlerAdapter {

    // 핸들러가 MemberFormControllerV3, MemberSaveControllerV3, MemberListControllerV3 이중 하나라면
    // 해당 어댑터를 사용하도록 참을 반환
    @Override
    public boolean supports(Object handler) {
        return (handler instanceof ControllerV3);
    }

    // 어댑터에서 핸들러를 받아서 핸들러(컨트롤러)의 로직 실행
    @Override
    public ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        // 핸들러가 Object이기 때문에 형변환 // 컨트롤러를 선택
        ControllerV3 controller = (ControllerV3) handler; // ex) MemberFormControllerV3

        // ----------------------------
        // 컨트롤러를 호출하기 전에 이 부분에서 공통로직과 같은 것을 처리할 수 있음
        // ----------------------------

        // createParamMap(request) => paramMap 생성
        Map<String, String> paramMap = createParamMap(request);

        // 4. handler 호출
        // 컨트롤러 호출, ModelView 반환 / paramMap을 인자로 함 (request, response => X)
        ModelView mv = controller.process(paramMap);

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
