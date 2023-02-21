package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import hello.servlet.web.frontcontroller.MyView;
import hello.servlet.web.frontcontroller.v3.controller.MemberFormControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberListControllerV3;
import hello.servlet.web.frontcontroller.v3.controller.MemberSaveControllerV3;
import hello.servlet.web.frontcontroller.v4.controller.MemberFormControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberListControllerV4;
import hello.servlet.web.frontcontroller.v4.controller.MemberSaveControllerV4;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV3HandlerAdapter;
import hello.servlet.web.frontcontroller.v5.adapter.ControllerV4HandlerAdapter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "frontControllerServletV5", urlPatterns = "/front-controller/v5/*")
public class FrontControllerServletV5 extends HttpServlet {

    // 핸들러 어댑터 목록 // MyHandlerAdapter로 다형성을 둠
    private final List<MyHandlerAdapter> handlerAdapters = new ArrayList<>();
    // 핸들러 매핑 정보 // Object로 다형성을 둠
    // 그 이유는 support나 handle에서 모든 버전의 Controller를 포괄할 수 있는 개념을 사용할 수 있어야하기 때문
    // 특정 컨트롤러로 두면 어댑터를 사용하는 의미가 없다.
    private final Map<String, Object> handlerMappingMap = new HashMap<>();

    // 핸들러 매핑 정보, 핸들러 어댑터 목록 초기화
    public FrontControllerServletV5() {     // 서블릿 컨테이너가 시작시 객체를 생성할 때 생성자 호출
        initHandlerMappingMap();
        initHandlerAdapters();
    }

    // 핸들러 어댑터 등록
    private void initHandlerAdapters() {
        // V3
        handlerAdapters.add(new ControllerV3HandlerAdapter());
        // V4
        handlerAdapters.add(new ControllerV4HandlerAdapter());
    }

    // 핸들러 등록
    private void initHandlerMappingMap() {
        // V3
        handlerMappingMap.put("/front-controller/v5/v3/members/new-form", new MemberFormControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members/save", new MemberSaveControllerV3());
        handlerMappingMap.put("/front-controller/v5/v3/members", new MemberListControllerV3());

        // v4
        handlerMappingMap.put("/front-controller/v5/v4/members/new-form", new MemberFormControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members/save", new MemberSaveControllerV4());
        handlerMappingMap.put("/front-controller/v5/v4/members", new MemberListControllerV4());
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 1. 핸들러 조회
        Object handler = getHandler(request);

        // 만약 핸들러가 없다면 404 에러 발생
        if(handler == null){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 2. 핸들러를 처리할 수 있는 핸들러 어댑터 조회
        MyHandlerAdapter adapter = getHandlerAdapter(handler);

        // ----------------------------
        // 특정 핸들러를 호출하기 전에 이 부분에서 공통로직과 같은 것을 처리할 수 있음 // 모든 버전에 적용하겠다..!! ==> 가능한가?
        // ----------------------------

        // ---------------- 여기까지가 버전을 찾아가는 로직 -------------------------------

        // 3. handler(handler) 메서드 호출 / 5. ModelView 반환
        ModelView mv = adapter.handle(request, response, handler);  // (Object)

        // 6. viewResolver 호출 (인자로 뷰의 논리 이름) / 7. MyView를 반환
        MyView view = viewResolver(mv.getViewName());
        // 8. render() 메서드 호출 / forward 모듈화 + model을 함께 전달
        view.render(mv.getModel(), request, response);

        // 줄거리 ...!!!!!
        // 모든 Controller의 상위 개념인 Object를 이용해서 매핑되어있는 핸들러가 어떤 버전인지 확인한 다음
        // 그에 따른 버전의 어댑터를 채택한다. 그 이후 핸들러와 함께 어댑터의 메서드 handle를 호출하면,
        // 핸들러의 버전에 따른 process를 실행하고, 해당 버전의 어댑터마다 획일화된 로직(handle 내부 로직)을 통해서 공통으로
        // ModelView를 반환하게 된다. 그리고 그 ModelView 방법에 따라 viewResolve를 호출하고 뷰의 render 메소드를 호출한다.

        // 핵심은 ...!!!!!
        // 따라서 MyHandlerAdapter의 다형성을 이용해서 어댑터 마다의 로직(handle)을 재정의 하고 Controller의 상위개념인 Object의 사용하여
        // 사용자는 컨트롤러의 버전에 개의치 않고 유연하게 서비스를 사용할 수 있다 (어떤 경로로 오던지 간에 사용이 가능해짐)
    }

    // 핸들러 어댑터 조회 (버전을 정함)
    private MyHandlerAdapter getHandlerAdapter(Object handler) {
        // 핸들러 어댑터 중에서
        for (MyHandlerAdapter adapter : handlerAdapters) {
            // 핸들러를 지원하는 어댑터를 찾음
            if(adapter.supports(handler)){  // (Object)
                // 찾았다면 해당 어댑터를 반환
                return adapter;
            }
        }
        // 찾지 못했다면 예외를 일으킴
        throw new IllegalArgumentException("handler adapter를 찾을수 없습니다. handler=" + handler);
    }

    // 뷰의 논리 이름을 통해서 MyView를 생성
    private static MyView viewResolver(String viewName) {
        // 기존 경로와 뷰의 논리 이름을 합쳐서 물리 경로를 만듬 -> MyView를 생성 -> 반환
        return new MyView("/WEB-INF/views/" + viewName + ".jsp");
    }

    // URL을 통해서 핸들러 조회
    private Object getHandler(HttpServletRequest request) {
        // URI를 받아서
        String requestURI = request.getRequestURI();
        // 핸들러 매핑정보에서 URI를 통해서 그에 맞는 핸들러를 받아옴 (Object)
        return handlerMappingMap.get(requestURI);
    }
}

