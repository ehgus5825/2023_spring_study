package hello.servlet.web.frontcontroller.v4;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV4", urlPatterns = "/front-controller/v4/*")
public class FrontControllerServletV4 extends HttpServlet {

    // 0. URL 매핑정보 생성

    // ControllerV4 <- MemberFormControllerV4
    //                 MemberSaveControllerV4
    //                 MemberListControllerV4

    private Map<String, ControllerV4> controllerMap = new HashMap<>();

    public FrontControllerServletV4() {
        controllerMap.put("/front-controller/v4/members/new-form", new MemberFormControllerV4());
        controllerMap.put("/front-controller/v4/members/save", new MemberSaveControllerV4());
        controllerMap.put("/front-controller/v4/members", new MemberListControllerV4());
    }

    @Override
    // Controller에 paramMap을 위임하기 때문에 Controller는 HttpServlet을 몰라도 됨. / 서블릿 종속성이 완전히 사라짐
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. URL 매핑 정보에서 컨트롤러 조회
        // URI를 받아서
        String requestURI = request.getRequestURI();

        // URL 매핑정보에서 URI를 통해서 그에 맞는 Controller을 받아옴
        ControllerV4 controller = controllerMap.get(requestURI);
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
        // model을 생성
        Map<String, Object> model = new HashMap<>();

        // 2. 컨트롤러 호출 / 3. viewName 반환 (뷰의 논리 이름)
        // paramMap과 model을 인자로 함 (request, response => X)
        String viewName = controller.process(paramMap, model);

        // 4. viewResolver 호출 (인자로 뷰의 논리 이름) / 5. MyView를 반환
        MyView view = viewResolver(viewName);
        // 6. render() 메서드 호출 / forward 모듈화 + model을 함께 전달
        view.render(model, request, response);  // (model -> request)

        // V3에서는 ModelView라는 매개체로 모델과 뷰 논리 이름을 컨트롤러로부터 받아왔지만
        // V4에서는 call by reference를 이용해서 컨트롤러에서 model을 변경하였고 viewName은 반환하는 형식으로 진행됨.
        // => 이러한 직관성과 편의성은 점진적인 리팩토링으로 귀결되는 결과이다.
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
