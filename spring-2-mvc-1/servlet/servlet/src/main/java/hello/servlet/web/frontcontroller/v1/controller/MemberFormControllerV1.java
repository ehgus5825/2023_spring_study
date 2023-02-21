package hello.servlet.web.frontcontroller.v1.controller;

import hello.servlet.web.ServletView;
import hello.servlet.web.frontcontroller.v1.ControllerV1;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class MemberFormControllerV1 implements ControllerV1 {

    @Override
    public void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 3. 컨트롤러에서 JSP forward
        String viewPath = "/WEB-INF/views/new-form.jsp";

        /* 기존 jsp로 이동해야하는 코드 ==> 실행이 되지 않음.. ㅠㅠ (webapp을 못찾음..)
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);
        */

        // jsp 대신 .. servlet으로 클래스 만들어서 대신찍음 ㅠㅠ
        ServletView.view(viewPath, request, response);
    }
}
