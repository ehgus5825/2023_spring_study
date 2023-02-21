package hello.exception.api;

import hello.exception.exception.BadRequestException;
import hello.exception.exception.UserException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id){

        // "/api/members/ex" 시 오류 발생
        if(id.equals("ex")){
            throw new RuntimeException("잘못된 사용자");
        }

        // "/api/members/bad" 시 오류 발생
        if(id.equals("bad")){
            throw new IllegalArgumentException("잘못된 입력 값");
        }

        // "/api/members/user-ex" 시 오류 발생
        if(id.equals("user-ex")){
            throw new UserException("사용자 오류");
        }

        // 아니라면 MeberDto 객체 반환
        return new MemberDto(id, "hello " + id);
    }

    // ResponseStatusExceptionResolver 예시 : @ResponseStatus
    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }

    // ResponseStatusExceptionResolver 예시 : ResponseStatusException
    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad", new IllegalArgumentException());
    }

    // DefaultHandlerExceptionResolver 예시
    @GetMapping("/api/default-handler-ex")
    public String defaultException(@RequestParam Integer data){
        return "ok";
    }
}
