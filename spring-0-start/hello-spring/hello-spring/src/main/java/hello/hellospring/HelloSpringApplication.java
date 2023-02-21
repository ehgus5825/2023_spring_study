package hello.hellospring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication // 컴포넌트 스캔 시작, hello.hellospring 패키지에서부터 아래로 컴포넌트를 찾고 의존관계를 주입함
public class HelloSpringApplication {
	// 해당 클래스의 메인을 실행하면 어노테이션인 @SpringBootApplication가 실행된다.
	// 스프링 부트는 톰캣 서버를 내장하고 있기 때문에 서버가 자동 실행됨
	public static void main(String[] args) {
		SpringApplication.run(HelloSpringApplication.class, args);
	}

}

// @Component ---@Autowired---> @Component ---@Autowired---> @Component
// 컴포넌트 내부에 있는 Autowired만 유효하고 Autowired가 붙은 생성자에서 의존관계가 있는 클래스에도 @Component 표시가 되어 있어야 한다.

// 컴포넌트 스캔으로 스프링 컨테이너에 올라온 객체들만 관리가됨...
// 따라서 테스트 코드에서 컴포넌트로 등록된 클래스의 생성자를 호출한다면 그 객체는 스프링 컨테이너의 관리를 받지 않음
// 그렇기 때문에 테스트에서 스프링의 관여 없이 순수 자바코드로만 가볍게 테스트가 가능하다.