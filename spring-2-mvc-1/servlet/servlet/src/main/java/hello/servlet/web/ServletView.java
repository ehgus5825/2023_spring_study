package hello.servlet.web;

import hello.servlet.domain.member.Member;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ServletView {

    public static void view(String viewPath ,HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("utf-8");

        if(viewPath.equals("/WEB-INF/views/new-form.jsp")) {
            PrintWriter w = response.getWriter();
            w.write("<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    " <meta charset=\"UTF-8\">\n" +
                    " <title>Title</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "<form action=\"save\" method=\"post\">\n" +
                    " username: <input type=\"text\" name=\"username\" />\n" +
                    " age: <input type=\"text\" name=\"age\" />\n" +
                    " <button type=\"submit\">전송</button>\n" +
                    "</form>\n" +
                    "</body>\n" +
                    "</html>\n");
        }
        else if (viewPath.equals("/WEB-INF/views/members.jsp")) {
            PrintWriter w = response.getWriter();
            w.write("<html>\n");
            w.write("<head>\n");
            w.write(" <meta charset=\"UTF-8\">\n");
            w.write(" <title>Title</title>\n");
            w.write("</head>\n");
            w.write("<body>\n");
            w.write("<a href=\"/index.html\">메인</a>\n");
            w.write("<table>\n");
            w.write(" <thead>\n");
            w.write(" <th>id</th>\n");
            w.write(" <th>username</th>\n");
            w.write(" <th>age</th>\n");
            w.write(" </thead>\n");
            w.write(" <tbody>\n");
            for (Member member : (List<Member>)request.getAttribute("members")) {
                w.write(" <tr>\n");
                w.write(" <td>" + member.getId() + "</td>\n");
                w.write(" <td>" + member.getUsername() + "</td>\n");
                w.write(" <td>" + member.getAge() + "</td>\n");
                w.write(" </tr>\n");
            }
            w.write(" </tbody>\n");
            w.write("</table>\n");
            w.write("</body>\n");
            w.write("</html>\n");
        } else if (viewPath.equals("/WEB-INF/views/save.jsp")) {
            PrintWriter w = response.getWriter();

            Member member = (Member)request.getAttribute("member");
            w.write("<html>\n" +
                    "<head>\n" +
                    " <meta charset=\"UTF-8\">\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "성공\n" +
                    "<ul>\n" +
                    " <li>id="+member.getId()+"</li>\n" +
                    " <li>username="+member.getUsername()+"</li>\n" +
                    " <li>age="+member.getAge()+"</li>\n" +
                    "</ul>\n" +
                    "<a href=\"/index.html\">메인</a>\n" +
                    "</body>\n" +
                    "</html>");
        }
    }
}
