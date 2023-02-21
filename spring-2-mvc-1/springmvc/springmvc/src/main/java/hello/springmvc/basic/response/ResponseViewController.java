package hello.springmvc.basic.response;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

// ------- 뷰 템플릿 호출 --------------------------------

@Controller
public class ResponseViewController {

    // ------- ModelAndView을 반환하는 경우 -------------------------

    @RequestMapping("/response-view-v1")
    public ModelAndView responseViewV1(){
        ModelAndView mav = new ModelAndView("response/hello")
                .addObject("data", "hello!");

        return mav;
    }

    // ------- String을 반환하는 경우 - View or HTTP 메시지(@RequestBody) -------------------------

    @RequestMapping("/response-view-v2")
    public String responseViewV2(Model model){
        model.addAttribute("data", "hello!");
        return "response/hello";
    }

    // ------- Void를 반환하는 경우 -------------------------

    // 권장하지 않음
    @RequestMapping("/response/hello")
    public void responseViewV3(Model model){
        model.addAttribute("data", "hello!");
    }
}
