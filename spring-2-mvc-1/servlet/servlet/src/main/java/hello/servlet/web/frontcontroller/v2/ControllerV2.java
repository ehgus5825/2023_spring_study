package hello.servlet.web.frontcontroller.v2;

import hello.servlet.web.frontcontroller.MyView;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ControllerV2 {

    // V2 : 해당 컨트롤러를 인터페이스로 하여 다형성 사용 + MyView를 반환해서 forward 모듈화
    MyView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}
