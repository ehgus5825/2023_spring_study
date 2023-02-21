package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v2.controller.MemberFormControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberListControllerV2;
import hello.servlet.web.frontcontroller.v2.controller.MemberSaveControllerV2;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV2", urlPatterns = "/front-controller/v2/*")
public class FrontControllerServletV2 extends HttpServlet {

    // 0. URL 매핑정보 생성

    // ControllerV2 <- MemberFormControllerV2
    //                 MemberSaveControllerV2
    //                 MemberListControllerV2

    private Map<String, ControllerV2> controllerMap = new HashMap<>();

    public FrontControllerServletV2() {
        controllerMap.put("/front-controller/v2/members/new-form", new MemberFormControllerV2());
        controllerMap.put("/front-controller/v2/members/save", new MemberSaveControllerV2());
        controllerMap.put("/front-controller/v2/members", new MemberListControllerV2());
    }

    @Override
    // Controller에 request, response를 위임하기 때문에 Controller는 HttpServlet을 몰라도 됨.
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. URL 매핑 정보에서 컨트롤러 조회
        // URI를 받아서
        String requestURI = request.getRequestURI();

        // URL 매핑정보에서 URI를 통해서 그에 맞는 Controller을 받아옴
        ControllerV2 controller = controllerMap.get(requestURI);
        // 만약 컨트롤러가 없다면 404 에러 발생
        if(controller == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // ----------------------------
        // 컨트롤러를 호출하기 전에 이 부분에서 공통로직과 같은 것을 처리할 수 있음
        // ----------------------------

        // 2. 컨트롤러 호출 / 3. MyView 반환
        // 아직까지는 request를 모델로 사용하고 있음.
        MyView view = controller.process(request, response);
        // 4. render() 메서드 호출 / forward 모듈화
        view.render(request, response);
    }
}
