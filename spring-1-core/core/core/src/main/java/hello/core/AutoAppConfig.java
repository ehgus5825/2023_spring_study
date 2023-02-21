package hello.core;

import hello.core.member.MemberRepository;
import hello.core.member.MemoryMemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan (
        // basePackages = "hello.core.member", // 설정한 패키지에서부터 찾음
        // basePackageClasses = AutoAppConfig.class, // 클래스의 패키지에서 부터 찾음
        // 지정하지 않으면 현재 클래스의 패키지에서 부터 찾음
        excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Configuration.class)
) // @Component를 찾아서 자동으로 다 스프링 컨테이너에 등록시킴
// 수동 빈 등록된것을 제외하기 위함
public class AutoAppConfig {

    
    
    /* 수동 빈 => 해당 수동 빈이 자동 수동빈 보다 우선권을 가짐 => 오버라이딩 됨
    @Bean(name = "memoryMemberRepository")
    MemberRepository memberRepository() {
        return new MemoryMemberRepository();
    }
    */
}