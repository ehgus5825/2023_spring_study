package hello.login.web.argumentresolver;

import hello.login.domain.member.Member;
import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {         // HandlerMethodArgumentResolver 구현

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        // 파라미터에 @Login 애노테이션이 있으면서 Member 타입이면 해당 ArgumentResolver가 사용된다.

        log.info("supportParameter 실행");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation(Login.class);
        boolean hasMemberType = Member.class.isAssignableFrom(parameter.getParameterType());

        return hasLoginAnnotation && hasMemberType;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        // supportsParameter의 조건(@Login이면서 Member 타입)에 부합해 resolveArgument를 실행

        // 해당 메소드에서 (컨트롤러 호출 직전에 호출되어 필요한 파라미터의 정보를 생성해서 반환)
        log.info("resolveArgument 실행");

        // HTTP로 다운 케스팅
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // 세션을 불러옴 (새로 세션을 생성하지 않음)
        HttpSession session = request.getSession(false);

        // 기존 세션이 없다면 파라미터로 null을 반환 [이전의 @SessionAttribute의 required = false와 동일]
        if(session == null){
            return null;
        }

        // 세션에 있는 로그인 회원 정보인 member 객체를 찾아서 반환
        return session.getAttribute(SessionConst.LOGIN_MAMBER);

        // 이후 반환된 Member 객체가 컨트롤러의 파라미터로 전달됨
    }
}
