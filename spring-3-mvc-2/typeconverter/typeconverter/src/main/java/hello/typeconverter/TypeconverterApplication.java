package hello.typeconverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TypeconverterApplication {

	public static void main(String[] args) {
		SpringApplication.run(TypeconverterApplication.class, args);
	}

}
/*
- 컨버터

	- 스프링 타입 컨버터 : 스프링은 문자, 숫자, 불린, Enum 등 일반적인 타입에 대한 대부분의 컨버터를 기본으로 제공한다.

	- 컨버터 인터페이스를 지원
		- Converter 인터페이스를 구현함.
		- 그리고 그 안에 convert 메소드를 오버라이딩하고 타입을 바꾸는 메소드를 넣음.
		- 컨버터를 일일이 생성하고 convert 메소드를 사용해서 타입을 바꾸는 것은 직접 타입을 바꾸는 것과 크게 다르지 않다.

	- 컨버전 서비스
		- 컨버전 서비스는 개별 컨버터를 모아두고 그것들을 묶어서 편리하게 사용할 수 있는 기능이다.
		- canConvert() : 컨버팅이 가능한지 확인 / convert() : 컨버팅 기능
		- DefaultConversionService 인스턴스를 선언하고 미리 정의해놓은 컨버터를 addConverter()해서 등록한다.
		- 등록과 사용의 분리 : 사용할 땐 타입 컨버터를 전혀 모르고 그냥 convert()를 호출해서 사용하면 된다.

		- 사용할 땐 convert("타입 변환전 변수 값", 변환될 타입의 클래스); 와 같이 사용하면 된다.
			- ex) Integer value = conversionService.convert("10", Integer.class)

		- DefaultConversionService는 두 인터페이스를 구현한다. (인터페이스 분리 원칙 - ISP)
			- ConversionService : 컨버터 사용에 초점
			- ConverterRegistry : 컨버터 등록에 초점

		- 컨버전 서비스는 @RequestParam, @ModelAttribute, @PathVariable, 뷰 템플릿 등에서 사용할 수 있다.

	- 스프링에 컨버터 등록
		- WebMvcConfigurer를 구현한 클래스(WebConfig)에서 addFormatter()를 오버라이딩하고 내부에 컨버터를 추가해주면
		- 스프링에서 타입을 변환할 때 자동으로 해당 컨버터가 작동한다.
			- ex) registry.addConverter(new StringToIntegerConverter());

		- 스프링이 내부에서 수 많은 기본 컨버터들을 제공하지만 직접 추가한 컨버터가 기본 컨버터들 보다 우선순위를 가짐.

	- 뷰 템플릿에 컨버터 적용하기
		- 타임리프는 렌더링 시에 컨버터를 적용해서 렌더링 하는 방법을 편리하게 지원한다.

		- 객체를 문자로 변환하는 작업.
			- 일반 변수표현식 : ${...}
				- 컨버젼 서비스를 적용하지 않으면 : 숫자 -> 문자(타임리프가 바꿔줌), 객체 -> toString()
			- 컨버젼 서비스 적용 ${{...}} / 자동으로 컨버전 서비스를 사용해서 반환된 결과를 출력해줌.
				- 컨버젼 서비스를 적용하면 : 숫자 -> 문자, 객체 -> 문자
		
		- 스프링과 통합되어서 제공하기 때문에 직접 만든 컨버터도 적용된다.
		- 타임리프의 th:field는 컨버젼 서비스도 지원 => ${{...}}와 동일
		
- 포맷터

	- 문자를 다른 타입으로 변환 or 다른 타입을 문자로 변환
	- 객체를 특정한 포맷에 맞추어 문자로 출력하거나 또는 반대의 역할을 하는 것에 특화된 기능이 바로 포맷터이다.
	- 숫자나 날짜에는 Locale(현지화) 정보가 사용될 수 있다.

	- Formatter 인터페이스
		- String print(T object, Locale locale) : 객체를 문자로 변경
		- T parse(String text, Locale locale) : 문자를 객체로 변경

	- 포멧터를 지원하는 컨버전 서비스
		- FormattingConversionService는 포맷터를 지원하는 컨버젼 서비스이다. 그리고 ConversionService 관련 기능을 상속받기 때문에 결과적으로
		  컨버터도 포맷터도 모두 등록가능.
		- DefaultFormattingConversionService는 FormattingConversionService에 기본적인 통화, 숫자 관련 몇가지 포맷터를 추가한 것임.
		- DefaultFormattingConversionService 인스턴스를 선언하고 미리 정의해놓은 포맷터를 addFormatter() 내부에 등록한다.

		- 사용할 땐 convert("타입 변환전 변수 값", 변환될 타입의 클래스); 와 같이 사용하면 된다.
			- ex) String str = conversionService.convert(1000, String.class)
	
	- 스프링에 포맷터 적용
		- WebMvcConfigurer를 구현한 클래스(WebConfig)에서 addFormatter()를 오버라이딩하고 내부에 포맷터를 추가해주면
		- 스프링에서 타입을 변환할 때 자동으로 해당 포맷터가 작동한다.
			- ex) registry.addFormatter(new MyNumberFormatter());

		- 컨버터가 포맷터보다 우선순위를 가진다. (범용성이 높고 구체적임)

	- 스프링이 제공하는 기본 포맷터
		- 애노테이션 기반으로 원하는 형식을 지정해서 사용할 수 있는 매우 유용한 포맷터 두 가지를 기본으로 제공

		- @NumberFormat : 숫자 관련 형식 지정 포맷터 사용, NumberFormatAnnotationFormatterFactory
		- @DateTimeFormat : 날짜 관련 형식 지정 포맷터 사용, Jsr310DateTimeFormatAnnotationFormatterFactory
		
		- 컨버터 때와 동일하게 ${{...}}를 사용하면 타임리프에서 포맷터 적용

	※ 메시지 컨버터는 컨버젼 서비스가 적용되지 않는다.
		- json 변환은 jackson 라이브러리를 사용해서 변환한다.
		- 따라서 json 결과로 만들어지는 숫자나 날짜 포맷을 변경하고 싶으면 해당 라이브러리가 제공하는 설정을 통해서 포맷을 지정해야한다.
*/