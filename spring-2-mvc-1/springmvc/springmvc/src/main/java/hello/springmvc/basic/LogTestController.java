package hello.springmvc.basic;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @Slf4j
@RestController     // @RestController는 반환 값이 String일 때 뷰를 찾는 것이 아니라 HTTP 메시지 바디에 바로 입력함
public class LogTestController {

    private final Logger log = LoggerFactory.getLogger(getClass());     // @Slf4j를 사용하면 생략 가능

    @RequestMapping("/log-test")
    public String LogTest(){
        String name = "Spring";

        System.out.println("name = " + name);

        // 로그 레벨에 해당되지 않아 사용하지 않아도 a+b 계산 로직이 먼저 실행됨, 이런 방식으로 사용하면 X
        // log.trace("trace my log=" + name);

        // 그러나 아래는 로그 레벨에 해당이 되지 않아 사용되지 않는다면 두 인자의 조합이 일어나지 않음, 이런 방식으로 사용 o
        log.trace("trace log={}", name);
        log.debug("debug log={}", name);
        log.info("info log={}", name);
        log.warn("warn log={}", name);
        log.error("error log={}", name);

        // 로그 출력 포맷 : 시간, 로그 레벨, 프로세스 ID, 쓰레드 명, 클래스명, 로그 메시지
        // 로그 레벨 : TRACE > DEBUG > INFO > WARN > ERROR

        // 로그는 application.properties에 설정된 로그 레벨에 따라서 출력이 됨
        // => 따라서 상황에 맞게 로그를 출력할 수 있음 / ex) 개발서버 -> debug / 운영서버 -> info 사용

        return "ok";
    }
}
