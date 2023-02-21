package hello.core.singleton;

public class SingletonService {
    // 싱글톤은 static으로 정의된 instance 객체 하나만을 사용하도록 하기 위한 문법 
    
    //1. static 영역에 객체를 딱 1개만 생성해둔다.
    // 공용으로 사용되는 인스턴스
    private static final SingletonService instance = new SingletonService();

    //2. public으로 열어서 객체 인스턴스가 필요하면 이 static 메서드를 통해서만 조회하도록 허용한다.
    // 공용 인스턴스가 사용될 수 있는 통로
    public static SingletonService getInstance() {
        return instance;
    }

    //3. 생성자를 private으로 선언해서 외부에서 new 키워드를 사용한 객체 생성을 못하게 막는다.
    // 해당 인스턴스를 생성할 수 없도록 막음 => 생성되는 인스턴스들의 속성인 instance는 모두 동일하겠지만 절차를 줄이고, 생성 방법을 하나로 줄임
    private SingletonService() {
        // new로 생성 후 인스턴스.getInstance()
        // 그냥 static 바로 클래스.getInstance()
    }
    
    public void logic() {
        System.out.println("싱글톤 객체 로직 호출");
    }
}
