package hello.exception;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExceptionApplication {
	public static void main(String[] args) {
		SpringApplication.run(ExceptionApplication.class, args);
	}
}

/*
- 예외 처리시 오류 페이지 출력

	- 방법 1. 서블릿 오류 페이지 등록

		- 컨트롤러1 -> 인터셉터 -> 필터 -> WAS -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러2

		- 컨트롤러1(ServletExController)에서 오류 발생/던짐 (ex. 404 오류)
		- Was는 오류 페이지 정보(WebServerCustomizer)를 확인하고 설정된 errorPage404에 맞게 Path에 맞는 컨트롤러를 다시 호출한다.
		- 호출된 컨트롤러2(ErrorPageController)는 매핑된 뷰를 출력한다. (error-page/404.html)
		- 클라이언트는 전혀 모른다. 서버 내부에서 호출하기 때문

		- 필터, 인터셉터의 재호출 매우 비효율적
			- 필터
				- DispatcherType 옵션 : 요청이 어떤 종류인지 구분하기 위한 옵션
				- request.getDispatcherType() : FORWARD, INCLUDE, REQUEST, ASYNC, ERROR
				- 고객 요청시 REQUEST, 오류 페이지 요청시 ERROR 전달됨
				- filterRegistrationBean.setDispatcherTypes()에 구체적인 디스패처 타입 정보를 넣으면 그 요청시 필터를 거침
				- 하지만 기본적으로 필터는 REQUEST 요청만 필터를 거치게 되있음. => 디폴트로 오류 페이지 요청은 필터를 거치지 않음
			- 인터셉터
				- 인터셉터는 서블릿이 아닌 스프링이 제공하는 기능이기 때문에 디스패치 타입과는 무관하게 항상 호출된다.
				- 따라서 이전에 배웠던 것과 동일하게 excludePathPatterns를 사용해서 빼주면 된다.

	- 방법 2. 스프링 부트 - 오류페이지 (BasicErrorController)

		- 스프링 부트는 "/error"라는 경로로 기본 오류 페이지를 설정한다. (이전에 WebServerCustomizer에 new ErrorPage()를 자동으로 정의)
		- BasicErrorController 라는 스프링 컨트롤러를 자동으로 등록 (이전에 예외 처리용 컨트롤러(ErrorPageController)를 만들었던 것과 동일)
		- 수동으로 등록한 오류 페이지가 없다면 스프링 부트가 자동 등록한 BasicErrorController는 "/error"를 기본으로 오류페이지를 처리함.
		- 개발자는 BasicErrorController가 제공하는 룰과 우선순위를 따라서 오류 페이지만 등록하면 된다. ("resources/templates/error/" 오류 페이지 생성(html))
		- 뷰템플릿 -> 정적 리소스 -> 뷰이름(error) 순으로 뷰를 선택함. / 404, 500와 같은 것이 5xx 처럼 덜 구체적인 것 보다 우선순위가 높다.
		- 오류 코드에 대한 뷰가 만들어져 있지 않으면 서블릿에서 만든 기본 오류 페이지를 보여준다.
			- server.error.whitelabel.enabled=true : 위처럼 오류화면을 못찾을 때, 스프링 whitelabel 오류 페이지 적용

		- BasicErrorController는 많은 정보를 model에 담아서 뷰에 전달한다.
			- 그러한 정보엔 timestamp, path, status, message, error, exception, errors, trace 등이 있다.
			- 그러나 이러한 정보들은 외부로 노출하는 것이 안좋기 때문에 기본적으로 노출되지 않는다.
			- 그렇기 때문에 혹시라도 노출하길 원한다면 "application.properties"의 아래 옵션을 추가해주면 된다.
				- server.error.include-exception=true : exception 포함 여부( true , false )
				- server.error.include-message=always : message 포함 여부 (never(사용X), always(항상 사용), on_param(파라미터가 있을시 O)
				- server.error.include-stacktrace=always : trace 포함 여부
				- server.error.include-binding-errors=always : error 포함 여부
			- 되도록이면 서버에 로그로 남겨서 오류를 해결해야한다.

		- server.error.path=/error : 오류 페이지 경로를 설정하는 옵션

- API 예외 처리

	- 방법 1. 직접 구현 (오류 페이지 컨트롤러 수정, 커스텀 오류 페이지, API 예외 구현)

		- 오류 페이지 컨트롤러에서 @RequestMapping에 "produces = MediaType.APPLICATION_JSON_VALUE"를 추가해서 직접 구현. (Accept가 json일 때)
		- 반환시 ResponseEntity나 @ResponseBody를 사용해서 Json 반환
		- 해당 방법으로 구현시 기존 @RequestMapping된 오류 페이지는 HTML을 출력함.
		- 해당 방법은 임의로 정의한 서블릿 오류 페이지를 설정하는 것이기 때문에 BasicErrorController를 사용하지 않음. (직접 구현한 것이 우선순위가 더 높음)
		- 오류에 대한 API 예외 처리를 이렇게 일일이 수행하는 것은 매우 고된 일이다.

	- 방법 2. 스프링 부트 기본 오류 처리 (BasicErrorController, 기본 오류 페이지 출력)

		- 해당 방법은 위에서 자동으로 오류 페이지를 출력해줬던 BasicErrorController를 사용하는 방법.
		- BasicErrorController 코드 내부에 위처럼 "/error"의 경로 produces 옵션이 적용되어 있어 Accept 값을 선택적으로 받을 수 있도록 되어 있음.
			- errorHtml() : produces = MediaType.TEXT_HTML_VALUE" 이기 때문에 Accept 값이 text/html인 경우에 호출되고 view를 제공
			- error() : Accept 값이 그 외인 경우에 호출되고 Json 데이터를 반환함.
		- 하지만 API 예외는 각각의 컨트롤러 마다 서로 다른 응답 결과를 출력해야하는 경우가 있다. => 복잡한 API 오류의 처리에는 마땅하지 않음

	- 방법 3. HandlerExceptionResolver를 사용하는 방법

		- HandlerExceptionResolver는 발생된 예외가 WAS로 전달되기 전에 중간에 가로채서 예외를 처리하고 정상 동작하도록 만드는 것이다.
		- 예외가 발생하면 설정된 webConfig에 등록된 HandlerExceptionResolver가 호출된다.
		- 핸들러가 호출된 후 디스패처 서블릿에서 HandlerExceptionResolver를 호출하며 request, response, 핸들러, 예외를 인자로 받는다.
		- resolveException에서 전해진 예외를 동적으로 처리할 수 있다.

		- 반환 값에 따른 동작 방식 :
			- 빈 ModelAndeView 반환 : new ModelAndView() 처럼 빈 ModelAndView 를 반환하면 뷰를 렌더링 하지않고, 정상 흐름으로 서블릿이 리턴된다.
			- ModelAndeView 지정 : ModelAndView 에 View , Model 등의 정보를 지정해서 반환하면 뷰를 렌더링한다.
			- null 반환 : null 을 반환하면, 다음 ExceptionResolver 를 찾아서 실행한다. 만약 처리할 수 있는 ExceptionResolver 가 없으면 예외
						 처리가 안되고, 기존에 발생한 예외를 서블릿 밖으로 던진다.

		- HandlerExceptionResolver 활용
			- 예외 상태 코드 변환, 뷰 템플릿 처리, API 응답 처리
			- 예외를 여기서 마무리 하기 : WAS를 거쳐 다시 컨트롤러에서 수행 X
								    => 바로 뷰를 렌더링하거나 서블릿 밖으로 response(json 매핑)를 출력

		- HandlerExceptionResolver를 사용하면 예외처리가 상당히 깔끔해지지만, 이걸 일일이 직접 구현하려고 하니 상당히 복잡하다.

	- 방법 4. 스프링이 제공하는 ExceptionResolver를 사용 (@ExceptionHandler)

		- ExceptionResolver의 종류 :

			- ResponseStatusExceptionResolver : HTTP 상태 코드를 지정 (변경)
				- @ResponseStatus :
					- @ResponseStatus 애노테이션에 상태코드(code)와, 오류 메시지(reason)를 지정하면 상태코드가 변경됨.
					- 메시지 기능도 있음 => reason을 MessageSource에서 찾도록 함
					- 개발자가 직접 변경할 수 없는 예외에는 적용할 수 없다. (애노테이션을 직접 넣어야함) => 조건에 따라 동적으로 변경 못함.
				- ResponseStatusException :
					- 개발자가 직접 변경할 수 없는 예외에도 상태 코드를 지정할 수 있음 => 동적으로 사용가능
					- new ResponseStatusException(변경할 오류 코드, 메시지, 기존 예외)로 설정하여 오류를 발생시키면 상태 코드를 변경할 수 있음.

			- DefaultHandlerExceptionResolver : 스프링 내부 기본 에외를 처리 (우선순위 가장 낮음)
				- ex) 파라미터 바인딩 시점 TypeMismatchException이 발생하면 500 코드가 아닌 400 코드를 사용하도록 되어있음.
				- 위와 같이 스프링 내부 오류를 어떻게 처리할지 수 많은 내용을 정의하고 있음.

			- ExceptionHandlerExceptionResolver : 제일 중요한 @ExceptionHandler을 처리, API 예외처리는 대부분 이 기능을 사용 (우선순위가 가장 높음)

				- API 예외든 오류페이지든 예외를 처리하기 가장 좋은 방법.
				- 이걸 사용하면 BasicErrorController나 HandlerExceptionResolver는 사용하지 않음.
					- BasicErrorController는 api 오류 응답을 커스텀해서 입맛에 맞게 예외를 내려줄 수 없었음
					- HandlerExceptionResolver는 api 오류 응답과 무관한 ModelAndView를 반환해줘야했음. 그리고 Response에 직접 응답 데이터를 넣어줘야 했음.
					- 그리고 특정 컨트롤러에서만 발생하는 예외를 별도로 처리하기가 어려움. (HandlerExceptionResolver에서 모든 컨트롤러에 대하여 일괄적으로 받기 때문)

				- @ExceptionHandler :
					- 마치 스프링 MVC의 컨트롤러처럼 작동한다.
					- 컨트롤러 내에 @ExceptionHandler를 선언하고 예외를 지정하면 해당 예외 발생시 해당 메서드가 호출된다. (그 예외의 자식 예외까지 모두 잡음)
					- 우선순위 : 자세한 것이 우위를 차지한다. (자식 > 부모)
					- 배열로 여러 예외를 지정할 수 있다. ex) @ExceptionHandler({AException.class, BException.class})
					- 파라미터의 예외 정보를 통해서 @ExceptionHandler의 예외를 생략할 수 있다.
					- @ResponseBody가 있으면 Json으로 반환, 없으면 View를 반환해야함.

					- 실행 흐름 (예시 IllegalArgumentException) :
						- IllegalArgumentException 예외 발생 => ExceptionResolver 작동 => 우선순위가 가장 높은 ExceptionHandlerExceptionResolver 실행
						 => 해당 컨트롤러에 IllegalArgumentException 예외를 처리할 수 있는 @ExceptionHandler가 있는지 확인
						 => 있다면 해당 메서드를 수행 (없다면 다른 ExceptionResolver를 호출하거나 BasicErrorController를 사용)
						 => 해당 메서드는 @RestController에 있기 때문에 @ResponseBody가 자동 적용
						 => HTTP 컨버터 사용, 응답이 JSON으로 반환된다. 그리고 지정된 @ResponseStatus(HttpStatus.BAD_REQUEST)에 따라 상태 코드가 400으로 응답.

					※ 만일 Accept가 "application/json"이 아니라면?
						- 적절한 HTTP 메시지 컨버터를 찾을 수 없어서 추가 예외가 발생함 -> BasicErrorController가 작동 -> 오류페이지 출력

					- @ExceptionHandler 사용시 반환값으로 ModelAndView 를 사용해서 오류 화면(HTML)을 응답하는데 사용할 수도 있다.
						- 하지만 잘 사용하지 않음 위의 추가 예외를 발생하게 해서 BasicErrorController를 작동하게 함.
						- 애초에 저렇게 사용하면 api 전용, 오류페이지 전용으로 2개의 @ExceptionHandler가 필요함. 하지만 충돌이남.
						- 그렇기 때문에 BasicErrorController를 작동하게 하는 것어 더 편리. => (?????? 잘 모르겠지만 걍 넘김 ??????)

			- @ControllerAdvice
 				- @ExceptionHandler로 예외를 깔끔하게 처리할 수 있게 되었지만, 정상 코드와 예외 처리 코드가 하나의 컨트롤러에 섞여 있음.
				- 그리고 여러 컨트롤러에서 공통으로 @ExceptionHandler를 공통으로 사용해서 API 오류 응답을 처리하고 싶다.
				- @ControllerAdvice를 사용하면 해결할 수 있다.

				- @ControllerAdvice는 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler, @InitBinder 기능을 부여해주는 역할을 한다
				- @ControllerAdvice에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다. (글로벌 적용)
				- @RestControllerAdvice = @ControllerAdvice + @ResponseBody

				- 대상 컨트롤러 지정 방법 :
					- 어노테이션 단위 :
						- @ControllerAdvice(annotations = RestController.class) /
						- @RestController가 달린 모든 컨트롤러
					- 특정 패키지 단위 :
						- @ControllerAdvice("org.example.controllers")
						- "org.example.controllers" 패키지 내의 모든 컨트롤러
					- 특정 클래스 단위 :
						- @ControllerAdvice(assignableTypes = {ControllerInterface.class, AbstractController.class})
						- ControllerInterface.class, AbstractController.class를 선언한 모든 컨트롤러

				※ 컨트롤러 내부에 있는 @ExceptionHandler와 특정 @ControllerAdvice로 부터 적용된 @ExceptionHandler의 충돌이 일어나면
				  컨트롤러 내부에 있는 @ExceptionHandler가 우선순위를 가진다.
 */
