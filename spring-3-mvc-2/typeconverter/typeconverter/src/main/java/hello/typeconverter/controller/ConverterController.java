package hello.typeconverter.controller;

import hello.typeconverter.type.IpPort;
import lombok.Data;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ConverterController {

    @Data
    static class Form {
        private IpPort ipPort;

        public Form(IpPort ipPort) {
            this.ipPort = ipPort;
        }
    }

    // 해당 모델의 값을 ${...} / ${{...}}의 유무에 따라 구분하는 코드
    @GetMapping("/converter-view")
    public String converterView(Model model){
        model.addAttribute("number", 10000);
        model.addAttribute("ipPort", new IpPort("127.0.0.1", 8080));
        return "converter-view";
    }

    // IpPort를 뷰 템플릿 폼에 출력한다. / 뷰에서 (IpPort -> String) // ${{...}}
    @GetMapping("/converter/edit")
    public String converterForm(Model model){
        IpPort ipPort = new IpPort("127.0.0.1", 8080);
        Form form = new Form(ipPort);
        model.addAttribute("form", form);
        return "converter-form";
    }

    // 뷰 템플릿 폼의 IpPort 정보를 받아서 출력한다.  /  @ModelAttribute 를 사용해서 (String -> IpPort)
    @PostMapping("/converter/edit")
    public String converterEdit(@ModelAttribute Form form, Model model){
        IpPort ipPort = form.getIpPort();
        model.addAttribute("ipPort", ipPort);
        return "converter-view";
    }
}
