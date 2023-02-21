package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 페이지 탐색 우선순위 : templates > static (따라서 index.html이 표시되지 않음)

    @GetMapping("/")    // 기본 링크 / "localHost:8080" 시 표시할 페이지
    public String home() {
        return "home";  // home.html을 찾는다.
    }
}
