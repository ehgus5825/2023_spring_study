package hello.servlet.web.frontcontroller.v5;

import hello.servlet.web.frontcontroller.ModelView;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// 핸들러 어댑터
public interface MyHandlerAdapter {

    // 핸들러가 해당 어댑터의 버전이 맞는지 아닌지 확인하여 true/false를 반환
    boolean supports(Object handler);

    // 핸들러를 받아서 어댑터마다의 내부 로직을 수행하여 ModelView를 반환
    ModelView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException;
}
