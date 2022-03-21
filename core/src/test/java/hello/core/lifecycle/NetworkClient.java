package hello.core.lifecycle;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

//public class NetworkClient implements InitializingBean, DisposableBean -- 1번 인터페이스 경우 사용
public class NetworkClient{

    private String url;

    public NetworkClient() {
        System.out.println("생성자 호출, url = " + url);
        connect();
        call("초기화 연결 메시지");
    }
    public void setUrl(String url) {
        this.url = url;
    }
    //서비스 시작시 호출
    public void connect() {
        System.out.println("connect: " + url);
    }
    public void call(String message) {
        System.out.println("call: " + url + " message = " + message);
    }
    //서비스 종료시 호출
    public void disConnect() {
        System.out.println("close: " + url);
    }

    //1. 인터페이스
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        connect();
//        call("초기화 연결 메시지");
//    }
//    @Override
//    public void destroy() throws Exception {
//        disConnect();
//    }
    //2. 빈 등록 초기화 및 소멸 메서드 지정
//    public void init() {
//        System.out.println("NetworkClient.init");
//        connect();
//        call("초기화 연결 메시지");
//    }
//    public void close() {
//        System.out.println("NetworkClient.close");
//        disConnect();
//    }

    //3. 애노테이션 사용
    @PostConstruct
    public void init() {
        System.out.println("NetworkClient.init");
        connect();
        call("초기화 연결 메시지");
    }
    @PreDestroy
    public void close() {
        System.out.println("NetworkClient.close");
        disConnect();
    }
}
