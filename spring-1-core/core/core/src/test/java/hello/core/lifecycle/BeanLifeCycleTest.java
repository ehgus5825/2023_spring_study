package hello.core.lifecycle;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

public class BeanLifeCycleTest {

    @Test
    public void lifeCycleTest() {
        // 상위 인터페이스이기 때문에 담을 수 있다.
        ConfigurableApplicationContext ac = new AnnotationConfigApplicationContext(LifeCycleConfig.class);
        NetworkClient networkClient = ac.getBean(NetworkClient.class);
        ac.close();
    }

    @Configuration
    static class LifeCycleConfig {

        @Bean // (initMethod =  "init", destroyMethod = "close") // 방법 2 // destroyMethod는 추론 기능이 있어서 생략이 가능함
        // 메소드의 로직 전부가 의존관계 주입
        // 2. 의존관계 주입 (실행)
        public NetworkClient networkClient() {
            NetworkClient networkClient = new NetworkClient();
            System.out.println("1. LifeCycleConfig.networkClient");
            networkClient.setUrl("http://hello-spring.dev");
            return networkClient;
        }
    }
}
