package hello.servlet.web.frontcontroller.v1;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface ControllerV1 {

    // V1 : 해당 컨트롤러를 인터페이스로 하여 다형성 사용
    void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
