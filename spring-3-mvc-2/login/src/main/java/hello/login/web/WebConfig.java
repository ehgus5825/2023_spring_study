package hello.login.web;

import hello.login.web.argumentresolver.LoginMemberArgumentResolver;
import hello.login.web.filter.LogFilter;
import hello.login.web.filter.LoginCheckFilter;
import hello.login.web.interceptor.LogInterceptor;
import hello.login.web.interceptor.LoginCheckInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.Filter;
import javax.servlet.FilterRegistration;
import java.util.List;

@Configuration  // 수동 스프링 빈 등록
public class WebConfig implements WebMvcConfigurer {        // WebMvcConfigurer 인터페이스 구현

    /**
     * ArgumentResolvers 확장
     */

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {       // WebMvcConfigurer의 구현 메소드

        resolvers.add(new LoginMemberArgumentResolver());         // addArgumentResolver로 LoginMemberArgumentResolver를 등록
    }

    /**
     * [ HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 스프링 인터셉터 -> 컨트롤러 ]
     */

    /**
     * 스프링 인터셉터 - 요청 로그, 인증체크
     */

    @Override
    public void addInterceptors(InterceptorRegistry registry){                  // WebMvcConfigurer의 구현 메소드

        // 스프링 인터셉터 - 요청 로그
        registry.addInterceptor(new LogInterceptor())                           // 인터셉터 등록 (요청 로그)
                .order(1)                                                       // 인터셉터의 호출 순서 지정 (낮을 수록 먼저)
                .addPathPatterns("/**")                                         // 인터셉터에 적용할 URL 패턴
                .excludePathPatterns("/css/**", "/*.ico", "/error");            // 인터셉터에 제외할 패턴 지정

        // 스프링 인터셉터 - 인증 체크
        registry.addInterceptor(new LoginCheckInterceptor())                    // 인터셉터 등록 (인증 체크)
                .order(2)                                                       // 요청로그 -> 인증체크 순
                .addPathPatterns("/**")                                         // 모든 URL에 인터셉터 적용
                .excludePathPatterns("/", "/members/add", "/login", "/logout",  // 이전의 필터에서 whiteList에 해당하는 URL
                        "/css/**", "/*.ico", "/error", "/session-info");        // => 인터셉터를 지정하지 않을 URL 지정


        // 스프링이 제공하는 URL 경로는 서블릿 기술이 제공하는 URL 경로와 완전히 다르다. 더욱 자세하고, 세밀하게 설정할 수 있다.
        // => PathPattern 공식 문서를 찾아보면 자세히 나와있음
    }

    /**
     * [ HTTP 요청 -> WAS -> 필터 -> 서블릿 -> 컨트롤러 ]
     */

    /**
     * 서블릿 필터 - 요청 로그
     *
     * - 스프링 부트를 사용한다면 FilterRegistrationBean를 사용해서 필터를 스프링 빈에 등록하면 된다.
     * - FilterRegistrationBean를 사용하면 필터의 순서 조절이 가능하다.
     */

    //@Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());      // 등록할 필터를 지정 (요청 로그)
        filterRegistrationBean.setOrder(1);                     // 인자의 숫자가 낮을 수록 필터가 먼저 동작함
        filterRegistrationBean.addUrlPatterns("/*");            // 필터를 적용할 URL 패턴을 지정

        return filterRegistrationBean;                          // filterRegistrationBean 반환
    }

    /**
     * 서블릿 필터 - 인증 체크
     */

    //@Bean
    public FilterRegistrationBean loginCheckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginCheckFilter());   // 등록할 필터를 지정 (인증 체크)
        filterRegistrationBean.setOrder(2);                         // 요청 로그 : 1 / 인증 체크 : 2 순으로 필터가 동작
        filterRegistrationBean.addUrlPatterns("/*");                // 모든 URL 패턴 체크

        return filterRegistrationBean;                              // filterRegistrationBean 반환
    }
}