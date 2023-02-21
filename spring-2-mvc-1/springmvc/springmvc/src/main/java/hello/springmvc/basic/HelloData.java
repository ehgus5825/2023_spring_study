package hello.springmvc.basic;

import lombok.Data;

@Data
public class HelloData {
    private String username;
    private int age;
}

/**
// MVC 기본 기능 정리
 - 요청
    - 매핑 :
        - @RequestMapping (+method)          // Http 메서드 매핑
        - @GetMapping (+ Post, Patch...)     // Http 메서드 매핑 축약                                          (!!!!)
        - @PathVariable (+ 다중, 생략가능)    // 경로변수                                                       (!!!!)
        - param 속성, header 속성, consumes 속성(Content-Type), produce 속성(Accept)
        - ※ 참고 : @Controller = @Component + @RequestMapping

    - 요청 헤더 :
        - @RequestHeader => Map, MultiValueMap
        - @RequestHeader => 특정 헤더                                                                         (!!!!)
        - @CookieValue (value, required)                                                                    (!!!!)

    - 요청 파라미터 : 쿼리파라미터, HTML Form
        - HttpServletRequest
        - 기본 타입 - @RequestParam (name 생략 가능, 자체 생략 가능, 필수, 기본값, Map, MultiValueMap)              (!!!!)
        - 객체 타입 - @ModelAttribute (자체 생략 가능)                                                          (!!!!)

    - 요청 메시지 : HTTP message body
        - Request, InputStream, HttpEntity, @RequestBody
            - @RequestBody는 생략할 수 없음. @RequestParam, @ModelAttribute과 겹치기 때문

        - 단순 텍스트 (String)
            - Request      : HttpServletRequest -> inputStream -> StreamUtils.copyToString() -> String
            - InputStream  : InputStream -> StreamUtils.copyToString() -> String
            - HttpEntity   : RequestEntity<String> -> .getBody() -> String
            - @RequestBody : @RequestBody [String] -> String                                                (!!!!)

        - Json(Object)
            - Request      : HttpServletRequest -> inputStream -> StreamUtils.copyToString() -> String -> objectMapper -> Object
            - InputStream  : inputStream -> StreamUtils.copyToString() -> String -> objectMapper -> Object
            - HttpEntity   : RequestEntity<Object> -> .getBody() -> Object
            - @RequestBody :
                - @RequestBody [String] -> String -> objectMapper -> Object
                - @RequestBody [Object] -> Object                                                           (!!!!)

 - 응답
    - 응답 헤더
        - @RequestStatus : HttpStatus.OK....
        - 응답 헤더를 자세하게 조합해서 보내기 위해서는 ResponseEntity<>의 기능을 사용하면 됨.

    - 정적 리소스 : 구현할 것이 X

    - 뷰 템플릿 :
        - ModelAndView를 반환하는 경우 :
            - ModelAndView 객체를 생성해서 논리 뷰 이름과 모델을 담아 반환.

        - String을 반환하는 경우 :
            - @ResponseBody가 없다면 :
                - String => 논리 뷰의 이름                                                                    (!!!!)
                - 인자에 Model이 있어 그곳에 전달 값을 저장
            - @ResponseBody가 있다면 :
                - String => HTTP 메시지 바디 내용
                - Http 메시지에서 자세히 설명

        - void를 반환하는 경우 :
            - HTTP 메시지 바디를 처리하는 파라미터가 없으면 @RequestMapping("URL")에 지정된 URL을 논리 뷰 이름으로 사용
                ㄴ> ex) HttpServletResponse, OutputStream(Writer)

    - HTTP 메시지 :
        - 단순 텍스트(String)
            - HttpServletResponse
                - response.getWriter().write("ok");
            - OutputStream(Writer) :
                - responseWriter.write("ok");
            - HttpEntity<String>
                - return new ResponseEntity<>("ok", HttpStatus.OK);
            - @ResponseBody :                                                                               (!!!!)
                - 기본 타입(String, ...)으로 반환시 메시지 바디에 찍힘
                - return "string";
        - Json(Object)
            - HttpServletResponse
                - response.getWriter().write(objectMapper.writeValueAsString(Object));
            - OutputStream(Writer) :
                - responseWriter.write(objectMapper.writeValueAsString(Object));
            - HttpEntity<Object>
                - return new ResponseEntity<>(Object, HttpStatus.OK);
            - @ResponseBody :                                                                               (!!!!)
                - 객체 타입(Object)으로 반환시 Json 형식으로 메시지 바디에 찍힘
                - return Object;

        - ※ 참고 : @RestController = @Controller + @ResponseBody =  @Component + @RequestMapping + @ResponseBody

 - 메시지 컨버터
    - viewResolver 대신 HttpMessageConverter 작동
    - viewResolver는 뷰템플릿으로 인식되었을 때 작동

    - HttpMessageConverter
        - canRead(), canWrite() : 메시지 컨버터가 해당 클래스, 미디어타입을 지원하는지 체크
        - read(), write() : 메시지 컨버터를 통해서 메시지를 읽고 쓰는 기능
        - Http 요청 데이터 읽기 : canRead() -> read()
        - Http 응답 데이터 생성 : canWrite() -> write()

 - ArgumentResolver :
    - 컨트롤러가 실행되기 전에 다양한 파라미터를 받아서 처리할 수 있도록 여러 파라미터의 형태를 정형화함
    - ex) HttpServletRequest, InputStream, Model, @RequestParam , @ModelAttribute, @RequestBody , HttpEntity ...
    - 이중 HttpEntity와 @RequestBody 사용시 HttpMessageConverter가 호출됨
        - text 요청 => HttpMessageConverter => StringHttpMessageConverter => 기본타입(String, int..) 파라미터
        - JSON 요청 => HttpMessageConverter => MappingJackson2HttpMessageConverter => 객체타입(Object) 파라미터

 - ReturnValueHandler :
    - 컨트롤러가 실행된 후 반환시 다양한 반환 형태를 처리할 수 있도록 반환값을 정형화 함
    - ex) ModelAndView , @ResponseBody , HttpEntity , String(view), OutputStream, HttpServletResponse ...
    - 이중 HttpEntity와 @ResponseBody 사용시 HttpMessageConverter가 호출됨
        - 기본타입(String, int..) 반환 => HttpMessageConverter => StringHttpMessageConverter => text 응답
        - 객체타입(Object) 반환 => HttpMessageConverter => MappingJackson2HttpMessageConverter => JSON 응답

 - RequestMappingHandlerAdapter -> ArgumentResolver -> Controller -> ReturnValueHandler -> RequestMappingHandlerAdapter...
                                        ↓ ↑                                 ↓ ↑
                                 HttpMessageConverter               HttpMessageConverter
                            (@RequestBody, HttpEntity일때)       (@ResponseBody, HttpEntity일때)

 */