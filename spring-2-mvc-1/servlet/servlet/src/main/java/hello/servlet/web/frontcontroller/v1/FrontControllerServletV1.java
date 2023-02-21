package hello.servlet.web.frontcontroller.v1;

import hello.servlet.web.frontcontroller.v1.controller.MemberFormControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberListControllerV1;
import hello.servlet.web.frontcontroller.v1.controller.MemberSaveControllerV1;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@WebServlet(name = "frontControllerServletV1", urlPatterns = "/front-controller/v1/*")
// /front-controller/v1/* : v1/~ 하위의 모든 경로가 다 받아짐
public class FrontControllerServletV1 extends HttpServlet {

    // 0. URL 매핑정보 생성

    // ControllerV1 <- MemberFormControllerV1
    //                 MemberSaveControllerV1
    //                 MemberListControllerV1

    // URL 매핑 정보 : ControllerV1 인터페이스의 구현체들을 담을 수 있는 Map
    private Map<String, ControllerV1> controllerMap = new HashMap<>();

    public FrontControllerServletV1() {     // 서블릿 컨테이너가 시작시 객체를 생성할 때 생성자 호출
        // 매핑 URL(key)에 따라 수행될 컨트롤러(value)를 모두 하나의 map에 담아둠 / 모든 컨트롤러는 ControllerV1을 구현하고 있음
        controllerMap.put("/front-controller/v1/members/new-form", new MemberFormControllerV1());
        controllerMap.put("/front-controller/v1/members/save", new MemberSaveControllerV1());
        controllerMap.put("/front-controller/v1/members", new MemberListControllerV1());
    }

    @Override
    // Controller에 request, response를 위임하기 때문에 Controller는 HttpServlet을 몰라도 됨.
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. URL 매핑 정보에서 컨트롤러 조회
        // URI를 받아서
        String requestURI = request.getRequestURI();

        // URL 매핑정보에서 URI를 통해서 그에 맞는 Controller을 받아옴
        ControllerV1 controller = controllerMap.get(requestURI);
        // 만약 컨트롤러가 없다면 404 에러 발생 / ex) /front-controller/v1/members/hello -> map에 없는 key임
        if(controller == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // ----------------------------
        // 컨트롤러를 호출하기 전에 이 부분에서 공통로직과 같은 것을 처리할 수 있음
        // ----------------------------

        // 2. 컨트롤러 호출
        controller.process(request, response);
    }
}
