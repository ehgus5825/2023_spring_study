package hello.servlet.web.servletmvc;

import hello.servlet.web.ServletView;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "mvcMemberFormServlet", urlPatterns = "/servlet-mvc/members/new-form")
public class MvcMemberFormServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 경로를 지정하고 담아서
        String viewPath = "/WEB-INF/views/new-form.jsp";

        /* 기존 jsp로 이동해야하는 코드 ==> 실행이 되지 않음.. ㅠㅠ (webapp을 못찾음..)
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request, response);      // request,response와 함께 해당 경로로 전달(jsp)
        */

        // jsp 대신 .. servlet으로 클래스 만들어서 대신찍음 ㅠㅠ
        ServletView.view(viewPath, request, response);
    }
}
