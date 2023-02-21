package hello.exception.exhandler.advice;

import hello.exception.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@RestControllerAdvice // (basePackages = "hello.exception.api")     // 해당 패키지에만 @ExceptionHandler 적용
public class ExControllerAdvice {

    // IllegalArgumentException 예외 발생시 400 코드 및 ErrorResult("BAD", e.getMessage())를 JSON으로 반환
    @ResponseStatus(HttpStatus.BAD_REQUEST)                             // 반환될 Http 상태 코드
    @ExceptionHandler(IllegalArgumentException.class)                   // 처리할 예외의 타입
    public ErrorResult illegalExHandler(IllegalArgumentException e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("BAD", e.getMessage());             // @RestController이기 때문에 JSON 반환
    }

    // UserException 예외 발생시 400 코드 및 ErrorResult("USER_EX", e.getMessage())를 JSON으로 반환
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandler(UserException e) { // 파라미터를 보고 처리할 예외의 타입을 생략 가능 
        log.error("[exceptionHandler] ex", e);
        ErrorResult errorResult = new ErrorResult("USER_EX", e.getMessage());
        return new ResponseEntity(errorResult, HttpStatus.BAD_REQUEST); // ResponseEntity 반환시 객체를 Json으로 변경
                                                                        // ResponseEntity 설정에 Http 코드를 지정
    }
    
    // Exception 예외 발생시 500 코드 및 ErrorResult("EX", "내부오류")를 JSON으로 반환 
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)                   // 반환될 Http 상태 코드
    @ExceptionHandler
    public ErrorResult exHandler(Exception e){
        log.error("[exceptionHandler] ex", e);
        return new ErrorResult("EX", "내부 오류");          // @RestController이기 때문에 JSON 반환
    }

}