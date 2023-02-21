package hello.core.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;   // 자바 공식
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class NetworkClient { // implements InitializingBean, DisposableBean {  // 방법 1.

    private String url;

    // 1. 생성자 호출 / 생성
    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    // 서비스 시작시 호출
    public void connect(){
        System.out.println("connect: = " + url);
    }

    public void call(String message){
        System.out.println("call : " + url + " message = " + message);
    }

    // 서비스 종료시 호출
    public void disconnect(){
        System.out.println("close : " + url);
    }

    // 방법 1.

    /*
    @Override
    // 3. 초기화
    public void afterPropertiesSet() throws Exception {
        // 의존관계 주입이 끝나면 호출
        System.out.println("2. NetworkClient.afterPropertiesSet");
        connect();
        call("초기화 연결 메시지");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("3. NetworkClient.destroy");
        disconnect();
    }
    */

    // 방법 2.

    /*
    // 3. 초기화
    public void init(){
        // 의존관계 주입이 끝나면 호출
        System.out.println("2. NetworkClient.init");
        connect();
        call("초기화 연결 메시지");
    }

    public void close(){
        System.out.println("3. NetworkClient.close");
        disconnect();
    }
    */

    // 방법 3.
    @PostConstruct
    public void init(){
        // 의존관계 주입이 끝나면 호출
        System.out.println("2. PostConstruct");
        connect();
        call("초기화 연결 메시지");
    }

    @PreDestroy
    public void close(){
        System.out.println("3. PreDestroy");
        disconnect();
    }
}

