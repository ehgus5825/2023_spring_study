package hello.servlet.web.frontcontroller.v3;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV3", urlPatterns = "/front-controller/v3/*")
public class FrontControllerServletV3 extends HttpServlet {

    // 0. URL 매핑정보 생성

    // ControllerV3 <- MemberFormControllerV3
    //                 MemberSaveControllerV3
    //                 MemberListControllerV3

    private Map<String, ControllerV3> controllerMap = new HashMap<>();

    public FrontControllerServletV3() {
        controllerMap.put("/front-controller/v3/members/new-form", new MemberFormControllerV3());
        controllerMap.put("/front-controller/v3/members/save", new MemberSaveControllerV3());
        controllerMap.put("/front-controller/v3/members", new MemberListControllerV3());
    }

    @Override
    // Controller에 paramMap을 위임하기 때문에 Controller는 HttpServlet을 몰라도 됨. / 서블릿 종속성이 완전히 사라짐
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. URL 매핑 정보에서 컨트롤러 조회
        // URI를 받아서
        String requestURI = request.getRequestURI();

        // URL 매핑정보에서 URI를 통해서 그에 맞는 Controller을 받아옴
        ControllerV3 controller = controllerMap.get(requestURI);
        // 만약 컨트롤러가 없다면 404 에러 발생
        if(controller == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // ----------------------------
        // 컨트롤러를 호출하기 전에 이 부분에서 공통로직과 같은 것을 처리할 수 있음
        // ----------------------------

        // createParamMap(request) => paramMap 생성
        Map<String, String> paramMap = createParamMap(request); // (request -> model)

        // 2. 컨트롤러 호출 / 3. ModelView 반환
        // paramMap을 인자로 함 (request, response => X)
        ModelView mv = controller.process(paramMap);

        // 4. viewResolver 호출 (인자로 뷰의 논리 이름) / 5. MyView를 반환
        MyView view = viewResolver(mv.getViewName());
        // 6. render() 메서드 호출 / forward 모듈화 + model을 함께 전달
        view.render(mv.getModel(), request, response);  // (model -> request)

        // controller의 서블릿 의존성을 없애기 위해 request -> param -> request 과정이 생김 >> 컨트롤러가 편해져야 한다..!!
    }

    // 뷰의 논리 이름을 통해서 MyView를 생성
    private static MyView viewResolver(String viewName) {
        // 기존 경로와 뷰의 논리 이름을 합쳐서 물리 경로를 만듬 -> MyView를 생성 -> 반환
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    // request의 모든 파라미터 정보를 다 복사해서 paramMap 반환.
    private static Map<String, String> createParamMap(HttpServletRequest request) {
        Map<String,String> paramMap = new HashMap<>();
        request.getParameterNames().asIterator()
                .forEachRemaining(paramName -> paramMap.put(paramName, request.getParameter(paramName)));
        return paramMap;
    }
}
