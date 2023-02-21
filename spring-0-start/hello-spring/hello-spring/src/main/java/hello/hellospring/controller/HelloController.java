package hello.hellospring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HelloController {
    
    // 정적 컨텐츠는 Controller를 거치지 않음
    
    // MVC 패턴 : 관심사 분리, 역할과 책임 --------------------------------------
    // 뷰는 화면을 그리는데 모든 역량을 집중
    // 컨트롤러나 모델은 비즈니스로직 내부적인 것을 처리하는데 집중

    @GetMapping("hello")
    // get방식으로 hello라는 페이지 요청시 매핑된 메소드(controller)를 읽음
    public String hello(Model model){
        model.addAttribute("data", "hello!!");
        // model에 데이터를 저장한 다음 리턴 값을 통해 hello.html 파일을 찾아서 데이터를 전송
        return "hello";
    }
    
    @GetMapping("hello-mvc")
    // @RequestParam : 외부에서 파라미터를 받음 (쿼리스트링)
    // @RequestParam의 required의 값이 true이기 때문에 기본적으로 값을 필수적으로 보내야함
    public String helloMvc(@RequestParam("name") String name, Model model) {
        // @RequestParam으로 받은 값을 모델에 담아서 hello-template.html로 보냄
        model.addAttribute("name", name);
        return "hello-template";

    }

    // API 방식 -------------------------------------------------------------
    
    // API (string)
    @GetMapping("hello-string")
    @ResponseBody
    // body 부분에 이 데이터를 내가 직접 넣어 주겠다. (HTTP의 BODY에 문자 내용을 직접 반환)
    // HttpMessageConverter => StringHttpMessageConverter
    public String helloString(@RequestParam("name") String name){
        return  "hello " + name; // "hello spring"
    }

    // API (Json)
    @GetMapping("hello-api")
    @ResponseBody
    // 객체를 보내면 json api 방식으로 보내짐 (기본)
    // HttpMessageConverter => MappingJackson2HttpMessageConverter
    public Hello HelloApi(@RequestParam("name") String name){
        Hello hello = new Hello();
        hello.setName(name);
        return hello;
    }

    static class Hello {
        private String name;

        // 게터,세터 : 자바빈 표준 방식, 프로퍼티 접근 방식
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
