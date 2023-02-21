package hello.hellospring;

import hello.hellospring.aop.TimeTraceApp;
import hello.hellospring.controller.MemberController;
import hello.hellospring.repository.*;
import hello.hellospring.service.MemberService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration  // 수동 스프링 빈 등록
public class SpringConfig {

    // 생성자와의 관계를 보고 의존관계를 유추

    /*
    private DataSource dataSource;
    private EntityManager em;

    @Autowired
    public SpringConfig(DataSource dataSource, EntityManager em){
        // resources에서 등록해뒀던 dataSourse
        this.dataSource = dataSource;       // jdbc, jdbcTemplate에서 사용
        this.em = em;                       // JPA에서 사용
    }
    */

    private final MemberRepository memberRepository;

    @Autowired
    public SpringConfig(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Bean  // MemberService -> MemberRepository
    public MemberService memberService(){
        return new MemberService(memberRepository);
    }

    /*
    // 스프링 데이터 JPA는 필요가 업음
    @Bean // MemberRepository
    public MemberRepository memberRepository(){
        // return new MemoryMemberRepository();

        // 의존관계를 외부에서 주입하고 있기 때문에 다른 코드를 변경할 필요없이
        // 아래의 코드로 바꿔주기만 하면 된다.
        // 그리고 그러한 의존관계 주입을 관리하는 스프링 컨테이너

        // JDBC
        // return new JdbcMemberRepository(dataSource);
        // return new JdbcTemplateMemberRepository(dataSource);

        // JPA
        // return new JpaMemberRepository(em);

        // SpringData JPA
    }
    */
}
