package hello.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


// 스프링 컨테이너 실행
@SpringBootApplication
public class CoreApplication {

	// CoreApplication.class 실행 -> @SpringBootApplication(@ComponentScan) -> 스프링 컨테이너 등록
	public static void main(String[] args) {
		SpringApplication.run(CoreApplication.class, args);
	}
}

// 스프링 부트로 실행시에는 "자동 빈 vs 수동 빈" 이더라도 수동 빈이 우선권을 가지지 않고 에러를 일으킴
