package hello.itemservice.web.validation;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import hello.itemservice.domain.item.SaveCheck;
import hello.itemservice.domain.item.UpdateCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/validation/v3/items")
@RequiredArgsConstructor
@Slf4j
public class ValidationItemControllerV3 {

    private final ItemRepository itemRepository;

    /**
     * 검증기를 등록할 필요가 없다. 알아서 다해줌.
     *
     * 기 정의해둔 검증기를 사용하는 것이 아니라 Bean Validation의 검증기를 사용함.
     * 해당 구현 부는 없고, domain에 애노테이션으만 적용하면 끝난다.
     *
     */

    @GetMapping
    public String items(Model model) {
        List<Item> items = itemRepository.findAll();
        model.addAttribute("items", items);
        return "validation/v3/items";
    }

    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/item";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("item", new Item());
        return "validation/v3/addForm";
    }

    /**
     * - 검증 순서 -
     * 1. @ModelAttribute 각각의 필드에 타입 변환 시도
     *   1-1. 성공하면 다음으로
     *   1-2. 실패하면 typeMismatch 로 FieldError 추가
     * 2. Validator 적용
     *
     * => 바인딩에 성공한 필드만 Bean Validation을 적용한다.
     *
     * - 오류 메시지 찾는 순서 -
     * 1. 생성된 메시지 코드 순서대로 messageSource 에서 메시지 찾기 (기 errors.properties에 정의된 메시지)
     * 2. 애노테이션의 message 속성 사용 @NotBlank(message = "공백! {0}")
     * 3. 라이브러리가 제공하는 기본 값 사용 공백일 수 없습니다.
     *
     * => 이전에 배웠던 메시지 자동 관리와 원리가 똑같다.
     */

    /**
     *  - V1의 한계점 :
     *    - Item 하나에 정의된 제한사항이 두 기능에서 다를 수 있다. (검증 조건의 충돌이 발생)
     *    - 수정에서와 등록에서의 요구사항이 다를 수 있다. 하지만 하나의 도메인 객체에 두 가지를 각각 등록하지 못한다.
     *    - => V2에서 groups 사용
     */

    // @PostMapping("/add")
    public String addItemV1(@Valid @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 특정 필드가 아닌 복합 룰 검증 (오브젝트 오류는 직접 정의)
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}", bindingResult);
            return "validation/v3/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    /**
     *  - groups :
     *    - 수정과 등록에 따른 마커 인터페이스를 만들어서 도메인의 Bean Validation에 groups로 지정해준다.
     *    - 수정, 등록 컨트롤러의 @Validated에 각각의 마커인터페이스를 적용해준다.
     *    - => 이렇게 하면 컨트롤러가 실행되고 검증기가 실행이되면 매핑이 된 Bean Validation만 작동한다.
     *    - @Valid 에는 groups 기능이 없다.
     *
     *  - V2의 한계점 :
     *    - 도메인 객체가 지저분해지고 전반적으로 복잡도가 올라간다.
     *    - 그래서 실무에서는 groups를 잘 사용하지 않고 등록용 폼 객체와 수정용 폼 객체를 분리해서 사용한다.
     */

    @PostMapping("/add")
    public String addItemV2(@Validated(SaveCheck.class) @ModelAttribute Item item, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}", bindingResult);
            return "validation/v3/addForm";
        }

        // 성공 로직
        Item savedItem = itemRepository.save(item);
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model) {
        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);
        return "validation/v3/editForm";
    }

    // @PostMapping("/{itemId}/edit")
    public String editV1(@PathVariable Long itemId, @Valid @ModelAttribute Item item, BindingResult bindingResult) {

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }

    @PostMapping("/{itemId}/edit")
    public String editV2(@PathVariable Long itemId, @Validated(UpdateCheck.class) @ModelAttribute Item item, BindingResult bindingResult) {

        // 특정 필드가 아닌 복합 룰 검증
        if (item.getPrice() != null && item.getQuantity() != null) {
            int resultPrice = item.getPrice() * item.getQuantity();
            if (resultPrice < 10000) {
                bindingResult.reject("totalPriceMin", new Object[]{10000, resultPrice}, null);
            }
        }

        // 검증에 실패하면 다시 입력 폼으로
        if(bindingResult.hasErrors()){
            log.info("errors={}", bindingResult);
            return "validation/v3/editForm";
        }

        itemRepository.update(itemId, item);
        return "redirect:/validation/v3/items/{itemId}";
    }
}

