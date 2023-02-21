package hello.springmvc.basic.request;


import com.fasterxml.jackson.databind.ObjectMapper;
import hello.springmvc.basic.HelloData;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


// ----- HTTP 요청 메시지 - JSON -----------------------------

/**
 * {"username":"hello", "age":20}
 * content-type: application/json
 */
@Slf4j
@Controller
public class RequestBodyJsonController {

    // --------------- requestBodyJson_V1 - Request, Response  -----------------------------

    private ObjectMapper objectMapper = new ObjectMapper();

    /**
     * HttpServletRequest
     * HttpServletResponse
     *
     * inputStream -> StreamUtils.copyToString() -> String -> objectMapper -> 객체변환 -> 출력
     */
    @PostMapping("/request-body-json-v1")
    public void requestBodJsonV1(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        String messageBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);

        log.info("messageBody={}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());

        response.getWriter().write("ok");
    }

    // --------------- requestBodyJson_V2 - @RequestBody 문자 변환 -----------------------------

    /**
     * @RequestBody
     * HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     *
     * @ResponseBody
     * - 모든 메서드에 @ResponseBody 적용
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> StringHttpMessageConverter 적용
     *
     * String -> objectMapper -> 객체 변환 -> 출력
     */
    @ResponseBody
    @PostMapping("/request-body-json-v2")
    public String requestBodJsonV2(@RequestBody String messageBody) throws IOException {
        log.info("messageBody={}", messageBody);
        HelloData helloData = objectMapper.readValue(messageBody, HelloData.class);
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    // --------------- requestBodyJson_V3 - @RequestBody 객체 변환 -----------------------------

    /**
     * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
     * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
     *
     * 처음부터 객체로 받음 -> 출력
     */
    @ResponseBody
    @PostMapping("/request-body-json-v3")
    public String requestBodJsonV3(@RequestBody HelloData helloData) throws IOException {
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    // --------------- requestBodyJson_V4 - HttpEntity -----------------------------

    /**
     * 특정 객체를 제네릭으로 받음 -> getBody()로 꺼냄 -> 출력
     */
    @ResponseBody
    @PostMapping("/request-body-json-v4")
    public String requestBodJsonV4(HttpEntity<HelloData> data) throws IOException {
        HelloData helloData = data.getBody();
        log.info("username={}, age={}", helloData.getUsername(), helloData.getAge());

        return "ok";
    }

    // --------------- requestBodyJson_V5 -----------------------------

    /**
     * @RequestBody 생략 불가능(@ModelAttribute 가 적용되어 버림)
     * HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter (content-type: application/json)
     *
     * @ResponseBody 적용
     * - 메시지 바디 정보 직접 반환(view 조회X)
     * - HttpMessageConverter 사용 -> MappingJackson2HttpMessageConverter 적용 (Accept: application/json)
     *
     * 처음부터 객체로 받음 -> 출력 -> 객체로 반환 (json으로 반환됨)
     */
    @ResponseBody
    @PostMapping("/request-body-json-v5")
    public HelloData requestBodJsonV5(@RequestBody HelloData data) throws IOException {
        log.info("username={}, age={}", data.getUsername(), data.getAge());

        return data;
    }
}
