package hello.core.scan.filter;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

// 설정 정보와 전체 테스트 코드
public class ComponentFilterAppConfigTest {

    @Test
    void filterScan(){
        ApplicationContext ac = new AnnotationConfigApplicationContext(ComponentFilterAppConfig.class);
        BeanA beanA = ac.getBean("beanA", BeanA.class);
        assertThat(beanA).isNotNull();


        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> ac.getBean("beanB", BeanB.class)
        );
    }

    // 설정 정보
    @Configuration
    @ComponentScan(
            // includeFilters : 컴포넌트 스캔 대상을 추가로 지정한다
            // excludeFilters : 컴포넌트 스캔에서 제외할 대상을 지정한다
            includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = MyIncludeComponent.class),
            // MyIncludeComponent 애노테이션이 적힌 클래스를 컴포넌트 추가 대상으로 지정
            excludeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = MyExcludeComponent.class)
            // MyExcludeComponent 애노테이션이 적힌 클래스를 컴포넌트 제외 대상으로 지정
    )
    static class ComponentFilterAppConfig{

    }

}

