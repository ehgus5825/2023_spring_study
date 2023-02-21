package hello.itemservice.web.validation;

import hello.itemservice.web.validation.form.ItemSaveForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/validation/api/items")
public class ValidationItemApiController {

    /**
     * - @Valid , @Validated는 HttpMessageConverter ( @RequestBody )에도 적용할 수 있다.
     *
     * - @ModelAttribute vs @RequestBody
     *   - @ModelAttribute : 필드 단위로 정교하게 바인딩이 적용된다. 특정 필드가 바인딩 되지 않아도 나머지 필드는 정상 바인딩 되고,
     *                       Validator를 사용한 검증도 적용할 수 있다.
     *   - @RequestBody : HttpMessageConverter 단계에서 JSON 데이터를 객체로 변경하지 못하면 이후 단계 자체가 진행되지 않고 예외가 발생한다.
     *                    컨트롤러도 호출되지 않고, Validator도 적용할 수 없다.
     */

    @PostMapping("/add")
    public Object addItem(@RequestBody @Validated ItemSaveForm form, BindingResult bindingResult){
        // 실패 요청(ex : typeError)은 여기에 불려지지도 않음 => JSON을 객체로 생성하는 것 자체가 실패함

        log.info("API 컨트롤러 호출");

        if(bindingResult.hasErrors()){
            // 검증 요청(ex : NotNull)은 검증에서 실패했지만 JSON을 객체로 생성하는 것은 성공함
            log.info("검증 오류 발생 errors={}", bindingResult);
            return bindingResult.getAllErrors();
            // ObjectError 와 FieldError를 JSON으로 반환
            // 실제 개발할 때는 이 객체들을 그대로 사용하지 말고, 필요한 데이터만 뽑아서 별도의 API 스펙을 정의하고 그에 맞는 객체를 만들어서 반환
        }

        log.info("성공 로직 실행");
        return form;
    }
}
