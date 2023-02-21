package hello.servlet.web.servletmvc;

import hello.servlet.domain.member.Member;
import hello.servlet.domain.member.MemberRepository;
import hello.servlet.web.ServletView;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "mvcMemberSaveServlet", urlPatterns = "/servlet-mvc/members/save")
public class MvcMemberSaveServlet extends HttpServlet {

    private MemberRepository memberRepository= MemberRepository.getInstance();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 입력
        String username = request.getParameter("username");
        int age = Integer.parseInt(request.getParameter("age"));

        // 서비스 로직
        Member member = new Member(username, age);
        memberRepository.save(member);

        //Model에 데이터를 보관한다.
        request.setAttribute("member", member);

        // 경로를 지정하고 담아서
        String viewPath = "/WEB-INF/views/save.jsp";

        /* 기존 jsp로 이동해야하는 코드 ==> 실행이 되지 않음.. ㅠㅠ (webapp을 못찾음..)
        RequestDispatcher dispatcher = request.getRequestDispatcher(viewPath);
        dispatcher.forward(request,response);       // request,response와 함께 해당 경로로 전달(jsp)
        */

        // jsp 대신 .. servlet으로 클래스 만들어서 대신찍음 ㅠㅠ
        ServletView.view(viewPath, request, response);
    }
}
