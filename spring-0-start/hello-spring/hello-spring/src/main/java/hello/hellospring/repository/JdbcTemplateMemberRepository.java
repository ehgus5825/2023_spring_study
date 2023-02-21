package hello.hellospring.repository;

import hello.hellospring.domain.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JdbcTemplateMemberRepository implements MemberRepository{

    // jdbc에서 템플릿 메소드 패턴을 사용했기 때문에 jdbcTemplate임
    // 스프링 JdbcTemplate과 MyBatis 같은 라이브러리는 JDBC API에서 본 반복 코드를 대부분 제거해준다. 하지만 SQL은 직접 작성해야 한다.
    private final JdbcTemplate jdbcTemplate;

    @Autowired  // 클래스 내 생성자가 하나라면 @Autowired 생략 가능
    public JdbcTemplateMemberRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 삽입
    
    @Override
    public Member save(Member member) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate);

        jdbcInsert.withTableName("member")                       // "member" 테이블에 삽입해라
                .usingGeneratedKeyColumns("id");    // "id" 컬럼의 값을 key로 반환해라.

        // 멤버의 이름을 map에 "name"[key]과 member.getName()[값]으로 매핑
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", member.getName());

        // 매핑한 값을 통해서 쿼리문 실행(executeAndReturnKey)한 다음 key를 반환 받음
        Number key = jdbcInsert.executeAndReturnKey(new MapSqlParameterSource(parameters));
        member.setId(key.longValue());  // key의 값을 통해서 member의 id를 설정
        return member;
    }

    // 조회
    
    @Override
    public Optional<Member> findById(Long id) {
        // ?에 id를 넣음
        List<Member> result = jdbcTemplate.query("select * from member where id = ?", memberRowMapper(), id);
        // 반환된 list에서 stream을 통해 하나만 반환
        return result.stream().findAny();
    }

    @Override
    public Optional<Member> findByName(String name) {
        // ?에 name을 넣음
        List<Member> result = jdbcTemplate.query("select * from member where name = ?", memberRowMapper(), name);
        // 반환된 list에서 stream을 통해 하나만 반환
        return result.stream().findAny();
    }

    @Override
    public List<Member> findAll() {
        // List를 반환하기 때문에 그냥 반환
        return jdbcTemplate.query("select * from member", memberRowMapper());
    }

    private RowMapper<Member> memberRowMapper(){
        // 람다식으로 변경!
        // 쿼리를 통해 조회된 객체를 멤버로 매핑 (rs -> member)
        return (rs, rowNum) -> {
            Member member = new Member();   // member 생성
            member.setId(rs.getLong("id")); // id 값 설정
            member.setName(rs.getString("name")); // 이름 설정
            return member; // member 반환
        };
    }
}
