package hello.springmvc.basic.requestmapping;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
public class MappingController {

    private Logger log = LoggerFactory.getLogger(getClass());

    // ---------------------------- 기본 매핑 ----------------------------

    /**
     * 기본 요청
     * 둘다 허용 /hello-basic, /hello-basic/ => spring 3.0 부터는 지원하지 않음, 서로 다른 경로임
     * HTTP 메서드 모두 허용 GET, HEAD, POST, PUT, PATCH, DELETE
     */
    // @RequestMapping({"/hello-basic", "/hello-go"})   // url 다중 설정
    @RequestMapping(value = "/hello-basic")             // method를 지정하지 않으면 다 받음
    public String helloBasic(){
        log.info("helloBasic");;
        return "ok";
    }

    // ---------------------------- HTTP 메서드 매핑 ----------------------------

    /**
     * method 특정 HTTP 메서드 요청만 허용
     * GET, HEAD, POST, PUT, PATCH, DELETE
     * HTTP 메서드에 맞지 않게 요청시 => HTTP 405(Method Not Allowed) 상태코드 반환
     */
    @RequestMapping(value = "/mapping-get-v1", method = RequestMethod.GET)  // GET 요청만 허용
    public String mappingGetV1(){
        log.info("mappingGetV1");;
        return "ok";
    }

    // ---------------------------- HTTP 메서드 매핑 축약 ----------------------------

    /**
     * 편리한 축약 애노테이션 (코드보기)
     * @GetMapping
     * @PostMapping
     * @PutMapping
     * @DeleteMapping
     * @PatchMapping
     */
    @GetMapping(value = "/mapping-get-v2")
    public String mappingGetV2() {
        log.info("mapping-get-v2");
        return "ok";
    }

    // ---------------------------- PathVariable(경로 변수) 사용 ----------------------------

    /**
     * PathVariable 사용
     * 변수명이 같으면 생략 가능
     * @PathVariable("userId") String userId -> @PathVariable userId
     * /mapping/userA ... {userId} => userA
     */
    @GetMapping("/mapping/{userId}")
    public String mappingPath(@PathVariable("userId") String data){ // (@PathVariable String userId)
        // {userId}를 data로 쓰겠다
        log.info("mappingPath userId={}", data);
        return "ok";
    }

    // ---------------------------- PathVariable(경로 변수) 사용 - 다중 ----------------------------

    /**
     * PathVariable 사용 다중
     */
    @GetMapping("/mapping/users/{userId}/orders/{orderId}")
    public String mappingPath(@PathVariable String userId, @PathVariable Long orderId) {
        log.info("mappingPath userId={}, orderId={}", userId, orderId);
        return "ok";
    }

    // ---------------------------- 특정 파라미터 조건 매핑 ----------------------------

    /**
     * 파라미터로 추가 매핑
     * params="mode",                       // mode가 있어야함
     * params="!mode"                       // mode가 없어야함
     * params="mode=debug"                  // mode가 debug여야함
     * params="mode!=debug" (! = )          // mode가 debug가 아니여아함
     * params = {"mode=debug","data=good"}  // mode가 debug여야하고 data도 good이여야 함
     * ...
     * "http://localhost:8080/mapping-param?mode=debug"으로 요청이 왔을 때만 실행됨 => 잘 사용되진 않음
     */
    @GetMapping(value = "/mapping-param", params = "mode=debug")
    public String mappingParam() {
        log.info("mappingParam");
        return "ok";
    }

    // ---------------------------- 특정 헤더 조건 매핑 ----------------------------

    /**
     * 특정 헤더로 추가 매핑
     * headers="mode",                  // mode라는 헤더가 있어야함
     * headers="!mode"                  // mode라는 헤더가 없어야함
     * headers="mode=debug"             // mode라는 헤더의 값이 debug여야함
     * headers="mode!=debug" (! = )     // mode라는 헤더의 값이 debug가 아니여야함
     * ...
     * "http://localhost:8080/mapping-header"의 요청 헤더에 mode가 값이 debug여야함.
     */
    @GetMapping(value = "/mapping-header", headers = "mode=debug")
    public String mappingHeader() {
        log.info("mappingHeader");
        return "ok";
    }

    // ---------------------------- 미디어 타입 조건 매핑 - HTTP 요청 Content-Type, consume ----------------------------

    /**
     * Content-Type 헤더 기반 추가 매핑 Media Type
     * 클라이언트에서 웹서버로 보내는 데이터의 형식을 담은 요청 헤더 (이러한 형식을 줄꺼야)
     * consumes="application/json"               // Content-Type이 application/json여야함
     * consumes="!application/json"              // Content-Type이 application/json가 아니여야함
     * consumes="application/*"                  // Content-Type이 application의 하위 타입이여야함
     * consumes="*\/*"                           // 모든 Content-Type이 허용
     * consumes=MediaType.APPLICATION_JSON_VALUE // == application/json
     * => 맞지 않으면 HTTP 415 상태코드(Unsupported Media Type)을 반환
     */
    @PostMapping(value = "/mapping-consume", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String mappingConsumes() {
        log.info("mappingConsumes");
        return "ok";
    }

    // ---------------------------- 미디어 타입 조건 매핑 - HTTP 요청 Accept, produce ----------------------------

    /**
     * Accept 헤더 기반 Media Type
     * 웹서버에서 클라이언트로 보내는 데이터의 형식을 담은 요청 헤더 (그럼 너는 이걸로 줘)
     * produces = "text/html"               // Accept가 text/html여야함
     * produces = "!text/html"              // Accept가 text/html가 아니여야함
     * produces = "text/*"                  // Accept가 text의 하위 타입이어야함
     * produces = "*\/*"                    // 모든 Accept의 값을 허용
     * => 맞지 않으면 HTTP 406 상태코드(Not Acceptable)을 반환
     */
    @PostMapping(value = "/mapping-produce", produces = "text/html")
    public String mappingProduces() {
        log.info("mappingProduces");
        return "ok";
    }
}
