package hello.itemservice.web.basic;

import hello.itemservice.domain.item.Item;
import hello.itemservice.domain.item.ItemRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/basic/items")
@RequiredArgsConstructor
public class BasicItemController {

    private final ItemRepository itemRepository;

    // 상품 목록 조회
    @GetMapping
    public String items(Model model) {                              // 모델
        // 비즈니스 로직
        List<Item> items = itemRepository.findAll();

        // 모델 사용
        model.addAttribute("items", items);

        return  "basic/items";                                      // 뷰 논리 이름
    }

    // 상품 상세 조회
    @GetMapping("/{itemId}")
    public String item(@PathVariable long itemId, Model model) {    // 경로 변수, 모델
        // 비즈니스 로직
        Item item = itemRepository.findById(itemId);

        // 모델 사용
        model.addAttribute("item", item);

        return "basic/item";                                        // 뷰 논리 이름
    }

    // 상품 등록 폼 (GET)
    @GetMapping("/add")
    public String addForm(){
        return "basic/addForm";                                     // 뷰 논리 이름
    }

    // 상품 등록 로직 (POST)

    // @PostMapping("/add")                        // @RequestParam로 값을 다 받아온 후 Item 객체 생성, 모델 저장 후 View 이동
    public String addItemV1(@RequestParam String itemName,          // 상품명
                            @RequestParam int price,                // 상품 가격
                            @RequestParam Integer quantity,         // 상품 수량
                            Model model){                           // 모델

        Item item = new Item();
        item.setItemName(itemName);
        item.setPrice(price);
        item.setQuantity(quantity);
        itemRepository.save(item);
        model.addAttribute("item", item);
        return "basic/item";
    }

    // @PostMapping("/add")                         // @ModelAttribute("item") 선언하면 모델에 ("item", item) 자동 추가, 모델 생략
    public String addItemV2(@ModelAttribute("item") Item itemObject){
        itemRepository.save(itemObject);
        // model.addAttribute("item", itemObject);
        return "basic/item";
    }

    // @PostMapping("/add")                         // name 생략 가능, 모델에 저장할 때 클래스명을 사용, 클래스의 첫글자만 소문자로 변경해서 등록 / Item => item
    public String addItemV3(@ModelAttribute Item item){
        itemRepository.save(item);
        return "basic/item";
    }

    // @PostMapping("/add")                         // @ModelAttribute 생략 가능
    public String addItemV4(Item item){
        itemRepository.save(item);
        return "basic/item";
    }

    // @PostMapping("/add")                         // 중복 저장 문제 해결 redirect => GET 호출 (PRG)
    public String addItemV5(Item item){
        // 비즈니스 로직
        itemRepository.save(item);
        return "redirect:/basic/items/" + item.getId();         // 인코딩 이슈가 있을 수 있음 => 위험
    }

    // 실제 사용.. v1 ~ v6..
    @PostMapping("/add")
    public String addItemV6(Item item, RedirectAttributes redirectAttributes){ // (..., Model model)
        // 비즈니스 로직
        Item savedItem = itemRepository.save(item);

        // RedirectAttributes 사용 : redirect 경로에 매핑되어서 경로에 추가
        redirectAttributes.addAttribute("itemId", savedItem.getId());
        redirectAttributes.addAttribute("status", true);

        return "redirect:/basic/items/{itemId}";                    // 뷰 논리 이름
    }

    // 상품 수정 폼 (GET)
    @GetMapping("/{itemId}/edit")
    public String editForm(@PathVariable Long itemId, Model model){
        // 비즈니스 로직
        Item item = itemRepository.findById(itemId);

        // 모델 사용
        model.addAttribute("item", item);

        return "basic/editForm";                                    // 뷰 논리 이름
    }

    // 상품 수정 로직 (POST)
    @PostMapping("/{itemId}/edit")
    public String edit(@PathVariable Long itemId, Item item){
        // 비즈니스 로직
        itemRepository.update(itemId, item);

        return "redirect:/basic/items/{itemId}";                    // 뷰 논리 이름
                                                                    // 중복 저장 문제 해결 redirect => GET 호출 (PRG)
    }

    /**
     * 테스트용 데이터 추가
     */
    @PostConstruct
    public void inin(){
        itemRepository.save(new Item("itemA", 10000, 10));
        itemRepository.save(new Item("itemB", 20000, 20));
    }

}
