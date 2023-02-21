package hello.servlet.web.frontcontroller;

import hello.servlet.web.ServletView;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

// MyView에서 하는 일
// 1. 뷰가 있는 경로를 저장
// 2. forward를 모듈화 해놓은 객체, View의 render 메서드를 통해서 forward (V2 / V3, V4)

public class MyView {
    private String viewPath;

    // 경로 저장
    public MyView(String viewPath) {
        this.viewPath = viewPath;
    }

    // V2에서 사용
    // 아직 request(모델)에 값이 담겨있음 / 컨트롤러에서 viewPath를 저장하여 반환, 그 경로를 통해서 forward
    public void render(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        // 5. JSP forward
        /* 기존 jsp로 이동해야하는 코드 ==> 실행이 되지 않음.. ㅠㅠ (webapp을 못찾음..)
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
        */

        // jsp 대신 .. servlet으로 클래스 만들어서 대신찍음 ㅠㅠ
        ServletView.view(viewPath, request, response);
    }

    // V3, V4에서 사용
    // model에 값이 담겨 있음 / 뷰 리졸버에서 생성된 myView의 viewPath, 그 경로를 통해서 forward
    public void render(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // view에서는 request를 통해 값을 사용해야하기 때문에 model에 있는 값을 request에 저장
        modelToRequestAttribute(model, request);
        // 5. JSP forward
        /* 기존 jsp로 이동해야하는 코드 ==> 실행이 되지 않음.. ㅠㅠ (webapp을 못찾음..)
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
        */

        // jsp 대신 .. servlet으로 클래스 만들어서 대신찍음 ㅠㅠ
        ServletView.view(viewPath, request, response);
    }

    // model에 담긴 모든 값을 request에 담는 메서드
    private static void modelToRequestAttribute(Map<String, Object> model, HttpServletRequest request) {
        model.forEach((key, value) -> request.setAttribute(key, value));
    }
}
