package hello.thymeleaf.basic;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/template")
public class TemplateController {

    @GetMapping("/fragment")
    public String template(){
        return "template/fragment/fragmentMain";
    }

    // "template/fragment/fragmentMain"로 이동

    // => th:insert="~{template/fragment/footer :: copy}" 속성이 적힌 태그가
    // => th:fragment="copy" 이 적힌 태그로 삽입 or 치환됨

    // => th:replace="~{template/fragment/footer :: copyParam ('데이터1', '데이터2')}" 속성이 적힌 태그가
    // => th:fragment="copyParam (param1, param2)" 속성이 적힌 태그로 삽입 or 치환됨
    // => 그러던 와중에 변수 표현식으로 ${param1}이나 ${param2}로 매핑된 부분에 각각 '데이터1', '데이터2'로 값이 들어감

    // insert or replace : insert는 해당 태그의 안에 삽입되고 replace는 치환됨
    // => 데이터만 넘긴 수준

    @GetMapping("/layout")
    public String layout() {
        return "template/layout/layoutMain";
    }

    // "template/layout/layoutMain"로 이동

    // => th:replace="template/layout/base :: common_header(~{::title},~{::link})" 속성이 적힌 태그가
    // title, link인 태그 자체를 파라미터로 넘겨서
    // => th:fragment="common_header(title,links)"이 적힌 태그를 호출하고 태그를 받아 해당 부분과 삽입 or 치환
    //     => title 태그를 받아온 title과 치환                     // (title과 ${title} 매핑)
    //     => 빈 태그 (<th:block/>)를 활용해서 link와 치환 (추가)   // (link와 ${link} 매핑)

    // 실제로 함수가 실행되는 곳이 템플릿이고, 사용자가 변경 or 추가하길 바라는 부분을 담아서 함수를 호출하면
    // 그 값과 함께 템플릿이 완성이되고 그걸로 하여금 HTML로 출력함
    // => 태그 자체를 넘긴 수준 // head 전체가 템플릿, head 내부의 태그들이 바뀜

    @GetMapping("/layoutExtend")
    public String layoutExtend(){
        return "template/layoutExtend/layoutExtendMain";
    }

    // "template/layoutExtend/layoutExtendMain"로 이동

    // => th:replace="~{template/layoutExtend/layoutFile :: layout(~{::title},~{::section})}" 속성이 적힌 태그가
    // title, section 태그 자체를 파라미터로 넘겨서
    // => th:fragment="layout (title, content)"이 적힌 태그를 호출하고 태그를 받아 해당 부분 삽입 or 치환
    //     => title 태그를 받아온 title과 치환               // (title과 ${title} 매핑)
    //     => div 태그와 치환                              // (link와 ${link} 매핑)

    // => 태그 자체를 넘긴 수준 // html 통째로 템플릿, html 내부의 태그들이 바뀜
}
