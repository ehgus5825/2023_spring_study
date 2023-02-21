package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/validation/v2/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV2 {

    private final ItemRepository itemRepository;
    private final ItemValidator itemValidator;      

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(itemValidator);
    }

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v2/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v2/addForm";
    }

    /**
     * V1 : BindingResult의 등장
     *
     *    - 파리머터에서 BindingResult은 항상 @ModelAttribute의 뒤에 있어야함.
     *    - BindingResult에 에러 객체를 넣는다.  // ex) bindingResult.addError(new 에러객체());
     *
     *    - 에러 객체는 FieldError와 ObjectError가 있다. 두 생성자는 아래와 같다.
     *      - public FieldError(String objectName, String field, String defaultMessage) {}
     *      - public ObjectError(String objectName, String defaultMessage) {}
     *      - objectName : @ModelAttribute 이름 / field : 오류가 발생한 필드 이름 / defaultMessage : 오류 기본 메시지
     *
     *    - bindingResult.hasErrors() 메소드를 통해서 에러의 유무를 보고 검증 실패시 이전 입력 폼으로 돌려보낸다. (BindigReuslt와 함께)
     *
     *    - BindingResult가 있으면 오류 정보(FieldError)를 BindingResult 에 담아서 컨트롤러를 정상 호출한다.
     *      - 타입 오류도 BindingResult에 담아서 반환한다. => 타입 에러시 400에러가 발생하지 않음
     *      - 이전에는 타입 오류시에는 컨트롤러가 실행되기 전에 에러가 발생하여 중지되었다.
     *
     *    - 한계 : 입력 폼 이전 값 유지가 안됨, 오류 메시지에 대한 관리가 어려움. 코드가 주렁주렁 길어져서 불편함. 
     */

    // @PostMapping("/add")
    public String addItemV1(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item","price", "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999 까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 = " + resultPrice ));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /** V2 : FieldError, ObjectError 생성자 확장 - 폼 이전 값 유지
     *
     *    - FieldError는 오류 발생시 사용자 입력 값을 저장하는 기능을 제공한다.
     *      - 타입 에러가 아니라면 직접 FieldError 생성하여 BindingResult에 담아둔다.
     *      - 타입 에러시라도 스프링은 일단 FieldError를 생성해서 BindingResult에 값을 담아둠.
     *      - => 따라서 이전 입력 폼으로 돌아가더라도 값을 유지할 수 있다.
     *
     *    - FieldError와 ObjectError의 생성자 :
     *      - public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure,
     *                        @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {}
     *      - public ObjectError(String objectName, @Nullable String[] codes, @Nullable Object[] arguments,@Nullable String defaultMessage) {}
     *      - => rejectedValue : 오류 발생시 사용자 입력 값을 저장하는 필드
     *      - => bindingFailure : 타입 오류 같은 바인딩이 실패 했는지 여부 (아니라면 false)
     *      - => FieldError 생성시 rejectedValue 필드를 추가해주면 그 값이 이전 입력 폼에 유지가 된다. 하지만 그렇게 하기 위해서 다른 인자들도 함께 넣어주어여한다.
     *      - => ObjectError()에 rejectedValue와 bindingFailure가 없는 이유:
     *          - 입력을 받는 것이 아니고 입력 받은 값을 조합해서 만들어지는 것이기 때문에 타입 에러가 안일어나고 거절된 값 자체가 없음
     *
     *    - 한계 : 오류 메시지에 대한 관리가 어려움. 코드가 주렁주렁 길어져서 불편함. 
     */

    // @PostMapping("/add")
    public String addItemV2(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품 이름은 필수입니다."));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item","price", item.getPrice(), false, null, null, "가격은 1,000 ~ 1,000,000 까지 허용합니다."));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999 까지 허용합니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 = " + resultPrice ));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * V3 : 생성자 확장 - 오류 코드 메시지 불러오기
     *
     *    - 오류 메시지 또한 이전에 배웠던 메시지,국제화처럼 사용할 수 있다.
     *    - application.properties에 spring.messages.basename=messages,errors 추가. (error.properties 사용하기 위함)
     *
     *    - FieldError와 ObjectError의 생성자 :
     *      - public FieldError(String objectName, String field, @Nullable Object rejectedValue, boolean bindingFailure,
     *                              @Nullable String[] codes, @Nullable Object[] arguments, @Nullable String defaultMessage) {}
     *      - public ObjectError(String objectName, @Nullable String[] codes, @Nullable Object[] arguments,@Nullable String defaultMessage) {}
     *      - => codes : 메시지 코드, String 배열로 등록된다. 배열 순서대로 매칭해서 처음 매칭되는 메시지가 사용됨.
     *      - => arguments : 메시지에서 사용하는 인자, Object 배열로 등록된다. 배열의 값이 메시지 코드의 {0}, {1} 같은 곳에 값이 치환됨
     *      - => defaultMessage : 메시지 코드에 설정해놓은 메시지가 없다면 출력되는 기본 오류 메시지
     *      
     *     - 한계 : 메시지 관리는 가능하지만 메시지 코드를 수동으로 등록해서 관리해야함, 코드가 주렁주렁 길어져서 불편함.
     */

    // @PostMapping("/add")
    public String addItemV3(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item","price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin"}, new Object[]{10000, resultPrice}, null));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * V4 : rejectValue, reject 메서드 사용
     *
     *    - BindingResult는 검증해야할 객체인 target을 알고 있다. 따라서 생략 가능. (@ModelAttribute가 적용된 Item)
     *    - FieldError와 ObjectError의 생성 대신, rejectValue(), reject()를 사용하면 된다.
     *    - rejectValue(), reject()가 오류코드 생성 후 알아서 FieldError와 ObjectError를 만들어 줌
     *
     *    - rejectValue(), reject()의 선언부 :
     *    - void rejectValue(@Nullable String field, String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
     *    - void reject(String errorCode, @Nullable Object[] errorArgs, @Nullable String defaultMessage);
     *      - field : 오류 필드명
     *      - errorCode : 오류 코드
     *      - errorArgs : 오류 메시지에서 치환될 매개변수
     *      - defaultMessage : 오류 메시지가 없을 때 사용될 기본 메시지
     *
     *    - 축약이 되는 이유 :
     *      - 1. BindingResult은 target(ObjectName) 알고 있기 때문에 자동적으로 "오류코오드.ObjectName.오류필드명"으로 류 코드를 매핑해서 오류 메시지를 사용함.
     *      - 2. target을 알고 있기 때문에 rejectedValue(거절된 입력값의 값)도 필요가 없음
     *      - => 그것 뿐만 아니라 레벨을 나누어 오류 코드를 자동으로 만들어서 사용한다.
     *          - ex) bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
     *          - =>  bindingResult.rejectValue("price", new String[]{"range.item.price", "range.price", "range.java.lang.Integer", "range"}, new Object[]{1000, 1000000}, null);
     *
     *    - 이처럼 자동으로 오류 코드를 생산해서 배열로 적용해주는 기능은 사용자로 하여금 "errors.properties"를 수정해주는 것만으로도 범용성이 높은 오류 메시지와 세밀한 오류 메시지를 입맛에 맞게 골라서 사용할 수 있다.
     *    - => 일반적으로 "range" 오류 코드만 사용하다가 특정 target의 필드명의 오류 메시지를 errors.properties에 등록해주는 것만으로도 자동으로 관리가 된다.
     *    - => 오류 코드에 관해 우선수위를 적용해서 범용성과 세밀함 모두 다 잡을 수 있는 방법임.
     *
     *    - 오류 코드를 자동으로 만들어주는 기준 (우선순위 순) :         => MessageCodesResolver를 자동으로 호출해서 생성됨
     *    - rejectValue (필드 오류)
     *      - 1. :  code + "." + object name + "." + field      / ex) range.item.price
     *      - 2. : code + "." + field                           / ex) range.price
     *      - 3. : code + "." + field type                      / ex) range.java.lang.Integer
     *      - 4. : code                                         / ex) range
     *    - reject (객체 오류)
     *      - 1. : code + "." + object name                     / ex) totalPriceMin.item
     *      - 2. : code                                         / ex) totalPriceMin
     *
     *    - 지금까지 Type 에러에 대한 오류 메시지를 설정 해주지 않았다. Type 에러는 자동으로 FieldError 객체를 생성하는데 그 와 관련된 에러 코드는 typeMismatch이다.
     *    - => 따라서 errors.properties에 typeMismatch에 대한 오류 메시지를 설정 해주면 된다. 그러면 알아서 작동한다.
     *    - ex)
     *      - typeMismatch.java.lang.Integer=숫자를 입력해주세요
     *      - typeMismatch=타입 오류입니다.
     *
     *    - 정리 :
     *      - 1. rejectValue() 호출
     *      - 2. MessageCodesResolver 를 사용해서 검증 오류 코드로 메시지 코드들을 생성
     *      - 3. new FieldError() 를 생성하면서 메시지 코드들을 보관
     *      - 4. th:erros 에서 메시지 코드들로 메시지를 순서대로 메시지에서 찾고, 노출
     *
     *    - 한계 : 컨트롤러의 역할이 너무 많다. 역할을 쪼개자. 모듈화가 조금 필요하다.
     */

    // @PostMapping("/add")
    public String addItemV4(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증 로직
        // ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "itemName", "required");
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.rejectValue("itemName", "required");
        }
        if (item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if (item.getQuantity() == null || item.getQuantity() >= 9999){
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null){
            int resultPrice = item.getPrice() * item.getQuantity();
            if(resultPrice < 10000){
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * V5 : validate 사용
     *
     *    - Validator 인터페이스를 구현하고 그 곳에 검증 코드를 분리해놓음
     *      - supports() {} : 해당 검증기를 지원하는 여부 확인
     *      - validate(Object target, Errors errors) : 검증 대상 객체와 BindingResult
     *      
     *    - ItemValidator를 스프링 빈으로 주입 받아서 직접 호출했다 
     *      - ItemValidator에 @Component
     *      - ValidationItemControllerV2에 @RequiredArgsConstructor를 통한 생성자 주입
     */

    // @PostMapping("/add")
    public String addItemV5(@ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        if(itemValidator.supports(item.getClass())) {
            itemValidator.validate(item, bindingResult);
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    /**
     * V6 : WebDataBinder 사용
     *
     *    - Validator 인터페이스를 사용해서 검증기를 만들면 스프링의 추가적인 도움을 받을 수 있다.
     *
     *    - WebDataBinder는 스프링의 파라미터 바인딩의 역할을 해주고 검증 기능도 내부에 포함한다.
     *    - 이렇게 WebDataBinder에 검증기를 추가하면 해당 컨트롤러에서는 검증기를 자동으로 적용할 수 있다. @InitBinder 해당 컨트롤러에만 영향을 준다.
     *      - ex) dataBinder.addValidators(itemValidator);
     *
     *    - @Validated는 검증기를 실행하라는 애노테이션이다. 이 애노테이션이 붙으면 앞서 WebDataBinder 에 등록한 검증기를 찾아서 실행한다.
     *    - 그런데 여러 검증기를 등록한다면 그 중에 어떤 검증기가 실행되어야 할지 구분이 필요하다.
     *    - 이때 supports() 가 사용된다. 여기서는 supports(Item.class) 호출되고, 결과가 true 이므로 ItemValidator 의 validate() 가 호출된다.
     *    - @Valid 사용 가능 ( @Valid : javax , @Validated : spring) - implementation 'org.springframework.boot:spring-boot-starter-validation' 라이브러리 추가
     *
     *    - ※ 글로벌 설정 :
     *      - ItemServiceApplication에 WebMvcConfigurer를 구현하고 getValidator()를 오버라이딩 하고,
     *      - 그 안에 검증기를 등록하면 모든 컨트롤러에 대해서 검증기가 작동한다. (@ModelAttribute 앞에 @Validated가 붙은 것만)
     */

    @PostMapping("/add")
    public String addItemV6(@Validated @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}", bindingResult);
            return "validation/v2/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v2/editForm";
    }

    //@PostMapping("/{itemId}/edit")
    public String editV1(@PathVariable Long itemId, @ModelAttribute Item item, BindingResult bindingResult) {

        // 검증 로직
        if(item.getId() == null){
            bindingResult.addError(new FieldError("item", "id", "id가 비어 있습니다."));
        }
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", "상품명을 입력해주세요"));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item", "price", "가격은 1,000원 이상 1,000,000원 이하여야 합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 10000){
            bindingResult.addError(new FieldError("item", "quantity", "수량은 최대 9,999개 까지 입력할 수 있습니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getQuantity() != null && item.getPrice() != null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

    // @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @ModelAttribute Item item, BindingResult bindingResult) {

        // 검증 로직
        if(item.getId() == null){
            bindingResult.addError(new FieldError("item", "id", item.getId(), false, null, null, "id가 비어 있습니다."));
        }
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, null, null, "상품명을 입력해주세요"));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, null, null, "가격은 1,000원 이상 1,000,000원 이하여야 합니다."));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 10000){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, null, null, "수량은 최대 9,999개 까지 입력할 수 있습니다."));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getQuantity() != null && item.getPrice() != null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", null, null, "가격 * 수량의 합은 10,000원 이상이어야 합니다. 현재값 = " + resultPrice));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

    // @PostMapping("/{itemId}/edit")
    public String editV3(@PathVariable Long itemId, @ModelAttribute Item item, BindingResult bindingResult) {

        // 검증 로직
        if(item.getId() == null){
            bindingResult.addError(new FieldError("item", "id", item.getId(), false, null, null, "id가 비어 있습니다."));
        }
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.addError(new FieldError("item", "itemName", item.getItemName(), false, new String[]{"required.item.itemName"}, null, null));
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.addError(new FieldError("item", "price", item.getPrice(), false, new String[]{"range.item.price"}, new Object[]{1000, 1000000}, null));
        }
        if(item.getQuantity() == null || item.getQuantity() >= 10000){
            bindingResult.addError(new FieldError("item", "quantity", item.getQuantity(), false, new String[]{"max.item.quantity"}, new Object[]{9999}, null));
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getQuantity() != null && item.getPrice() != null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice < 10000){
                bindingResult.addError(new ObjectError("item", new String[]{"totalPriceMin.item"}, new Object[]{10000, resultPrice}, null));
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

    // @PostMapping("/{itemId}/edit")
    public String editV4(@PathVariable Long itemId, @ModelAttribute Item item, BindingResult bindingResult) {

        // 검증 로직
        if(item.getId() == null){
            bindingResult.rejectValue("id", null, null, "id가 비어 있습니다.");
        }
        if(!StringUtils.hasText(item.getItemName())){
            bindingResult.rejectValue("itemName", "required", null, null);
        }
        if(item.getPrice() == null || item.getPrice() < 1000 || item.getPrice() > 1000000){
            bindingResult.rejectValue("price", "range", new Object[]{1000, 1000000}, null);
        }
        if(item.getQuantity() == null || item.getQuantity() >= 10000){
            bindingResult.rejectValue("quantity", "max", new Object[]{9999}, null);
        }

        // 특정 필드가 아닌 복합 룰 검증
        if(item.getQuantity() != null && item.getPrice() != null){
            int resultPrice = item.getQuantity() * item.getPrice();
            if(resultPrice < 10000){
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

    // @PostMapping("/{itemId}/edit")
    public String editV5(@PathVariable Long itemId, @ModelAttribute Item item, BindingResult bindingResult) {

        if(itemValidator.supports(item.getClass())) {
            itemValidator.validate(item, bindingResult);
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String editV6(@PathVariable Long itemId, @Validated @ModelAttribute Item item, BindingResult bindingResult) {

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={} ", bindingResult);
            return "validation/v2/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v2/items/{itemId}";
    }
}

