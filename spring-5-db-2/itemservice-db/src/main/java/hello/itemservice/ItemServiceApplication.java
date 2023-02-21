package hello.itemservice;

import hello.itemservice.config.*;
import hello.itemservice.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;


@Slf4j
//@Import(MemoryConfig.class)
//@Import(JdbcTemplateV1Config.class)
//@Import(JdbcTemplateV2Config.class)
//@Import(JdbcTemplateV3Config.class)
//@Import(MyBatisConfig.class)
//@Import(JpaConfig.class)
//@Import(SpringDataJpaConfig.class)
//@Import(QuerydslConfig.class)
@Import(V2Config.class)
@SpringBootApplication(scanBasePackages = "hello.itemservice.web")
public class ItemServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ItemServiceApplication.class, args);
	}

	@Bean
	@Profile("local")
	public TestDataInit testDataInit(ItemRepository itemRepository) {
		return new TestDataInit(itemRepository);
	}

	/*
	@Bean
	@Profile("test")
	public DataSource dataSource(){
		log.info("메모리 데이터베이스 초기화");
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("org.h2.Driver");
		dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
		dataSource.setUsername("sa");
		dataSource.setPassword("");
		return dataSource;
	}
	*/
}

/*

- 테스트

	- 데이터베이스 연동
		- test - application.properties 수정
		- @SpringBootTest는 @SpringBootApplication를 찾음 => 실행
		- local 설정을 인해 초기화 데이터는 추가되지 않지만 기존 데이터가 테스트에 방해가 됨.

	- 데이터베이스 분리
		- 데이터베이스 별도 운영 (h2 데이터베이스 testcase로 새로 팜)
		- test - application.properties에 URL 변경 => 테이블 생성
		- 처음 테스트를 실행하면 되지만 2번째부터는 데이터가 쌓여서 에러 발생
		- 서비스 db와 테스트 db를 분리하는 건 잘했지만, 테스트가 안됨.
			- 테스트는 다른 테스트와 격리해야한다.
			- 테스트는 반복해서 실행할 수 있어야한다.

	- 데이터 롤백
		- 단위 테스트 시작전 트랜잭션 시작 => 테스트 => 데이터 롤백
		- @BeforeEach(트랜잭션시작), @AfterEach(롤백)을 사용
		- 트랜잭션 매니저를 사용 (repository의 template는 알아서 트랜잭션 동기화 매니저의 커넥션을 사용함)
		- 하지만 이를 테스트마다 일일이 구현하기엔 번거롭다.

	- @Transactional
		- 테스트 클래스 위에 @Transactional 애노테이션 하나면 끝이남.
		- @Transactional이 테스트에 있으면 스프링은 테스트를 트랜잭션 안에서 실행하고, 테스트가 끝나면 트랜잭션을 자동으로 롤백
		- 테스트 케이스의 메서드나 클래스에 @Transactional을 직접 붙여서 사용할 때만 이와 같이 동작.
			- 그렇다면 서비스나 리포지토리에 있는 @Transactional와 충돌되면 어떻게 되는지? => 이후 트랜잭션 전파에서 설명
		- 강제 종료되어도 데이터베이스에서 커밋을 하지 않기 때문에 자동으로 롤백됨.
		- 필요에 따라 강제로 커밋할 수 도 있음 (@Commit or @Rollback(value = false)를 사용)

	- 임베디드 모드 DB
		- 테스트가 끝나면 데이터베이스 자체를 제거해도 된다.
		- H2 데이터베이스는 JVM안에서 메모리 모드로 동작하는 특별한 기능을 제공 => 임베디드 모드
		- 스프링 빈으로 DataSource 등록 => Url을 설정하는 메소드의 인자에 "jdbc:h2:mem:db;DB_CLOSE_DELAY=-1"라고 넣음.
			- @Profile("test")로 설정해서 test일 때만 등록되도록 적용.
			- jdbc:h2:mem:db : 임베디드 모드 사용
			=> 실행시 오류 발생 (데이터베이스를 새로 만들기 때문에 테이블이 없음)
			- 직접 URL로 데이터베이스를 등록했기 때문에 기존 application.properties의 등록 내용은 무시됨.
		- test에 resources에 scheama.sql을 넣고 그 안에 테이블 생성 sql을 담음
			- 기본 sql 스크립트이기 때문에 해당 파일의 sql문으로 데이터베이스를 초기화.

	- 스프링 부트와 임베디드 모드
		- 앞서 등록된 Datasource 주석처리 +  application.properties의 기존 DB 설정도 모두 주석처리
			- 이렇게 해도 test는 잘 돌아간다. 이유는?
		- 이렇게 DB 설정에 별다른 정보가 없으면 스프링 부트 임베디드 모드로 접근하는 데이터소스를 만들어서 제공한다.
			- 설정하지 않으면 스프링 부트가 앞서 DataSource를 설정했던 모든 일련의 행위를 자동으로 해준다.

- ItemRepository

	- MemoryItemRepository
		
		- 메모리를 저장소로 사용 / 시작시 데이터를 주입
		- save() : Map에 저장
		- update() : Map 내용 setter로 바꿈
		- findById() : get으로 Item 반환, Optional 반환
		- findAll() : stream으로 변환 후 필터링
		- clearStore() : 저장소 초기화

	- JdbcTemplateRepositoryV1 - 기본

		- JDBC 코드의 반복 문제를 해결해줌.
			- 순서 기반 파라미터 바인딩 지원.
			- 트랜잭션 매니저 사용, 데이터 커넥션 연결, 커넥션 해제 등 알아서 다해줌.

		- JdbcTemplate를 사용, DataSource를 주입 받음.
		- RowMapper<Item> :
			- 데이터베이스의 조회 결과를 객체로 변환할 때 사용. (ResultSet -> Item)

		- save() :
			- temaplate.update()에 람다식과 keyHodler을 순서대로 전달 (람다식에서 sql과 바인딩할 파라미터를 전달함)
			- keyHolder과 connection.prepareStatement(sql, new String[]{"id"})를 사용해서 PK id 조회
		- update() :
			- temaplate.update()에 sql과 '?'에 바인딩할 파라미터를 순서대로 전달
		- findById() :
			- template.queryForObject()에 sql과 RowMapper, 바인딩 파라미터를 순서대로 전달.
			- queryForObject()는 결과 로우가 하나일 때 사용. 없거나 두개 이상이면 예외 발생
			- Optional 처리
		- findAll() :
			- template.query()에 sql과 RowMapper, param(바인딩 파라미터)를 순서대로 전달.
			- query()는 결과 로우가 여러개일 때 사용. 결과가 없으면 빈 컬렉션 반환.
			- 동적 쿼리
				- param이라는 List에 조건에 따라 바인딩 파리미터를 넣음
				- 조건에 따라 sql에 구문을 추가함. 
				- cond가 완전히 비어있으면 "where"을 넣지 않음, andFlag는 "and"를 붙일지 말지에 대한 여부

		
	- JdbcTemplateRepositoryV2 - 이름 지정 파라미터

		- 이름 기반 파라미터 바인딩을 지원.
			- SQL의 빈 값과 파라미터의 key를 매핑해서 sql 바인딩의 모호함을 제거.

		- NamedParameterJdbcTemplate를 사용, DataSource를 주입 받음.

		- 이름 지정 파라미터 :
			- sql에서 '?' 대신에 ':파라미터이름'을 받음
			- 아래의 파라미터로 전달된 key는 ':파라미터이름'에 매핑이되고, value는 그 값이됨.
			- '?'에 직접 값을 넣어 바인딩하지 않아서 sql 구문이 달라지면 오류가 발생함. => 큰 오류를 잡음
			- sql 구문과 바인딩 파라미터의 매핑을 통해서 바인딩에 대한 모호홤을 제거함.

		- 이름 지정 바인딩에서 사용하는 파라미터 :
			- Map : 단순 Map 사용
			- SqlParameterSource : 인터페이스
				- MapSqlParameterSource : 메서드 체인 사용, sql 타입 지정 가능
					- addValue()로 key와 value를 매핑
				- BeanPropertySqlParameterSource : 자바빈 프로퍼티 규약을 통해서 파라미터 객체를 생성
					- 객체를 인자로 넣어서 해당 객체의 프로퍼티와 sql 구문의 ":파라미터이름"을 자동으로 바인딩함. (수와 이름이 정확히 일치해야함)
					- ex) getItemName()을 보고 key를 itemName으로 변환해서 만듬.

		- BeanPropertyRowMapper : ResultSet의 내용을 자바빈 규약에 맞추어 객체를 생성.
			- newInstance()에 조회 결과를 매핑해서 반환하고자 하는 객체를 넣고 반환.
			- SQL에 별칭을 사용해야하지만 관례 때문에 생긴 표기법 불일치를 자동으로 변환하여 바환해줌
				- 스네이크 표기법(SQL) -> 카멜 표기법(자바)
				- 그렇기 때문에 컬럼 이름과 객체 이름이 완전히 다르다르면 별칭을 사용하면 되고 아니면 나두면 됨.

		- save() :
			- 람다식이 아닌 BeanPropertySqlParameterSource()를 사용 => param
			- temaplate.update()에 sql, param, keyHolder을 순서대로 전달.
		- update() :
			- MapSqlParameterSource을 사용해서 ":파라미터이름"에 바인딩될 key와 value를 지정 => param
			- temaplate.update()에 sql, param을 순서대로 전달.
		- findById() :
			- Map을 통해서 ":파라미터이름"에 바인딩될 key와 value를 지정
			- template.queryForObject()에 sql, param, itemRowMapper()을 순서대로 전달 
			- itemRowMapper()은 BeanPropertyRowMapper을 통해 객체를 반환하는데 사용. => 람다식
			- EmptyResultDataAccessException 예외에 대비한 Optional 처리
		- findAll() :
			- BeanPropertySqlParameterSource()를 사용 => param
			- template.query()에 sql, param, itemRowMapper()을 순서대로 전달
			- itemRowMapper()은 BeanPropertyRowMapper을 통해 객체를 반환하는데 사용. => 람다식
			- 동적 쿼리 : V1과 동일

	- JdbcTemplateRepositoryV3 - SimpleJdbcInsert
		
		- Insert sql을 보다 쉽게 하기 위해 사용

		- SimpleJdbcInsert를 추가, DataSource를 주입 받음.
			- 메서드 체인으로 insert 설정 정보를 추가
			- withTable() : 데이터를 저장할 테이블 명을 지정.
			- usingGeneratedKeyColumns() : key를 생성하는 PK 컬럼 명을 지정
			- usingColumns() : Insert sql에 사용할 컬럼을 지정한다. 일부만 저장하고 싶을 때 사용
				- 생략가능, 생략하면 테이블의 전체 컬럼이 지정.
			 
		- save() : 아래의 내용을 제외하면 V2와 동일.
			- 위의 설정 정보를 통해서 알아서 sql이 생성됨.
			- template 사용 X
				- jdbcInsert.executeAndReturnKey()에 param만 넣어주면 된다.
				- sql은 알아서 생성되고 executeAndReturnKey()의 반환 값이 데이터베이스에서 생성된 PK 키 값이기 때문에 (sql, keyHolder 생략)
		- update() : V2와 동일
		- findById() : V2와 동일
		- findAll() : V2와 동일

	- mybatis

		- JdbcTemplate이 제공하는 대부분의 기능을 제공
		- SQL을 XML에 편리하게 작성할 수 있고 동적 쿼리를 매우 편리하게 작성할 수 있음.
		- XML에 작성하기 때문에 라인이 길어져도 문자 더하기에 대한 불편함이 없다.

		- 설정이 조금 불편함. => 라이브러리 등록, application.properties
			- mybatis.type-aliases-package=hello.itemservice.domain : 패키지 이름 생략
			- mybatis.configuration.map-underscore-to-camel-case=true : 언더바를 카멜로 자동 변경해주는 기능
			- logging.level.hello.itemservice.repository.mybatis=trace : 쿼리 로그를 확인

		- ItemMapper.java(인터페이스) <=> ItemMapper.xml

			- 매핑 방법 :
				- 일단 둘의 패키지 위치를 맞춰야함.
					- ItemMapper.java는 "/java/" 에서 시작, ItemMapper.xml는 "/resources/"에서 시작
				- ItemMapper.java : @Mapper 애노테이션 => MyBatis에서 인식함 => xml의 해당 SQL을 실행하고 결과를 돌림
				- ItemMapper.xml : <mapper namespace"">에 ItemMapper.java의 경로를 지정.

			- 문법 특징 :
				- insert, update, select, delete 태그의 id의 이름과 동일한 인터페이스 메서드와 매핑됨.
					- ex. save() <-> <insert id="save">
				- 인터페이스에서 파라미터에 @Param("이름")으로 설정한 객체의 값을 사용함.
					- 인터페이스에서는 넘길 객체가 하나라면 @Param("param") 생략 가능,
					- xml 파일에서는 #{ } 내부에 넘긴 객체의 프로퍼티 이름을 적어 사용하면 됨. / ex) #{param.item}
						- 내부적으로 PreparedStatement를 사용해서 ?와 치환함.
						- 넘긴 객체가  param 하나라면 프로퍼티 이름으로만 사용가능 / ex) #{item}
				- xml에서는 reusultType 속성에 반환 타입을 명시하면 됨.
					- Item을 적었는데 이는 "패키지 이름 생략"을 적용해놓았기 때문임.
					- BeanPropertyRowMapper 처럼 결과를 편리하게 객체로 바로 변환해줌.
					- "언더바를 카멜로 자동 변경해주는 기능"을 적용해두어 자동으로 변환해줌. (item_name => itemName)

			- save() <-> <insert id="save">
				- <insert> 사용
				- id는 save를 적용
				- 인터페이스에서 Item을 보냈기 때문에 item의 프로퍼티를 "item." 없이 사용 / ex) #{price}
				- useGeneratedKeys는 데이터베이스가 키를 생성해주는 IDENTITY 전략일 때 사용 / ex) useGeneratedKeys="true"
				- keyProperty는 생성되는 키의 속성 이름(id)을 지정, insert가 끝나면 item 객체의 id 속성에 값을 입력해줌.

			- update() <-> <update id="update">
				- <update> 사용
				- id는 update를 적용
				- 인터페이스에서는 id와 updateParam을 보냈기 때문에 @Param을 생략 못함. / ex) #{updateParam.price}
				
			- findById() <-> <select id="findById">
				- <select> 사용
				- reusultType에 Item을 반환. => BeanPropertyRowMapper 처럼 편리하게 객체로 매핑
				- 반환 객체가 1개이기 때문에 인터페이스의 반환값인 Optional<Item>로 반환해줌.
			
			- findAll() <-> <select id="findAll">
				- <select> 사용
				- reusultType에 Item을 반환. => BeanPropertyRowMapper 처럼 편리하게 객체로 매핑
				- 반환 객체가 n개이기 때문에 인터페이스의 반환값인 List<Item>로 반환해줌.
				- 동적 쿼리 : <where>, <if> 같은 동적 쿼리 문법 사용
					- <if> 는 해당 조건(test="")이 만족하면 구문을 추가
					- <where> 은 적절하게 where 문장을 만들어준다.
						- <if> 가 모두 실패하게 되면 SQL where 를 만들지 않는다.
						- <if> 가 하나라도 성공하면 처음 나타나는 and 를 where 로 변환해준다.

		- MyBatisItemRepository :
			- save() : ItemMapper에 위임
			- update() : ItemMapper에 위임
			- findById() : ItemMapper에 위임
			- findAll() : ItemMapper에 위임

			- ItemMapper는 인터페이스임. 구현체가 아님.
				- MyBatis 스프링 연동 모듈에서 동적 프록시 기술을 사용해서 구현체를 만들어준다.
				- 추가로 구현체는 스프링 예외 추상화도 함께 적용된다.
				- 데이터 커넥션, 트랜잭션 기능도 마이바티스와 함께 연동하고 동기화 해줌.

	- JPA

		- 도메인 객체와 테이블 매핑.
			- Item : @Entity, @Id, @GeneratedValue, @Column
			- 기본 생성자 작성.

		- JpaItemRepositoryV1
			- @Repository : 스프링 추상화 예외를 적용시킴.
			- @Transactional : JPA의 모든 데이터 변경(등록, 수정, 삭제)은 트랜잭션 안에서 이루어져야 한다. 따라서 @Transactional 필수
			- EntityMapper : JPA의 모든 동작은 EntityMapper를 이용, 데이터소스를 내부에 가지고 있고 데이터베이스에 접근.

			- save() :
				- em.persist(item) 실행
				- 단순 쿼리이기 때문에 JPA가 알아서 SQL을 만듬.
					- insert into item (item_name, price, quantity) values (?, ?, ?)
					- IDENTITY PK 키 전략을 사용.
			- update() :
				- item = em.find(); item.setItemName(); item.setPrice(); item.setQuantity(); 실행
				- 단순 쿼리이기 때문에 JPA가 알아서 SQL을 만듬.
					- update item set item_name=?, price=?, quantity=? where id=?
					- 트랜잭션 커밋 시점에 JPA가 변경된 엔티티 객체를 찾아서 쿼리를 수행.
			- findById() - 단건 조회
				- em.find(id) 실행
				- 단순 쿼리이기 때문에 JPA가 알아서 SQL을 만듬.
					- select .....
			- findAll() - 목록 조회
				- 단순 쿼리가 아니기 때문에 jpql 작성
					- select i from Item i....
					- 조건문을 이용해서 jpql을 완성시켜야함.
				-  TypeQuery<> query = em.createQuery(jpql);
					- query.setParameter() : jpql의 파라미터에 값을 매핑
					- query.getResultList() : 쿼리 실행 후 resultSet의 값을 List로 변환

			- 단순 쿼리에 대해서는 편리하게 사용할 수 있지만. 동적 쿼리에는 사용하기 어렵다는 단점이 존재.
			- 객체와 SQL 테이블을 매핑시켜준다는 점에서 SQL에 대한 의존성을 줄여줄 수 있다

	- Spring Data JPA :

		- 공통 인터페이스 기능 (인터페이스에서 메서드를 공통으로 사용)
			- 스프링 데이터(Repository <- CrudRepository <- PagingAndSortingRepository) <- 스프링 데이터 JPA(JpaRepository)
		- 쿼리 메서드 기능
			- 메서드 이름을 분석해서 쿼리를 자동으로 만들고 실행해주는 기능을 제공

		- SpringDataJapItemRepository (extends JpaRepository<엔티티, 엔티티ID>)
			- JpaRepository 인터페이스에 공통 메소드가 있음.
			- SpringDataJapItemRepository에 직접 정의한 추상메소드가 있음.
			- 그렇게 하면 자동으로 SpringDataJapItemRepository에 + JpaRepository가 되어 프록시를 이용해 구현체를 생성.
			- 사용시에는 SpringDataJapItemRepository에 의존하여 메소드를 사용하면 됨.

			- JpaRepository 공통 메서드
				- persist(), setXXX(), find(), findAll()....

			- SpringDataJapItemRepository 직접 정의한 메소드
				- findByItemNameLike(itemName)
					- 테이블의 itmaName의 값과 인자의 itemName의 값이 일치한다면 그 row 모두 조회
				- findByPriceLessThanEqual(maxPrice)
					- 테이블의 price의 값이 인자의 maxPrice의 값보다 작다면 그 row 모두 조회
				- findByItemNameLikeAndPriceLessThanEqual(itemName, maxPrice)
					- 위의 둘 조건에 모두 부합한다면 그 row 모두 조회
					- 쿼리 메서드 기능 사용 => 분석해서 jpql을 자동으로 만듬.
				- findItems(itemName, maxPrice) : @Query를 통해 jpql 작성
					- 위의 둘 조건에 모두 부합한다면 그 row 모두 조회
					- 쿼리 메서드 기능이 삭제됨.

		- JpaItemRepositoryV2 => SpringDataJapItemRepository에 의존
			- save() :
				- repository.persist() 실행
				- JpaItemRepository 때와 동일
			- update() :
				- item = em.find(); item.setItemName(); item.setPrice(); item.setQuantity(); 실행
				- JpaItemRepository 때와 동일
			- findById() :
				- repository.find() 실행
				- JpaItemRepository 때와 동일
			- findAll()
				- itemName이나 maxPrice의 값에 따라 아래의 메소드를 선택해서 호출
					- repository.findAll()
						- 해당 메소드만 japRepository의 공통 메서드이다.
						- itemName과 maxPrice가 모두 비어있다면 호출
					- repository.findByItemNameLike(itemName)
						- itemName만 비어있지 않다면 호출
					- repository.findByPriceLessThanEqual(maxPrice)
						- maxPrice만 비어있지 않다면 호출
					- repository.findItems(itemName, maxPrice) : jpql 작성
						- itemName과 maxPrice가 모두 비어있지 않다면 호출

		- ItemService에서 바로 SpringDataJapItemRepository를 바로 사용할 수 있지만
		- ItemService는 ItemRepository 에 의존하기 때문에 ItemService에서 SpringDataJapItemRepository를 사용할 수 없다.
		- 그렇기 때문에 중간 작업을 해주는 JpaRepository가 필요했다. (어댑터 처럼 사용)

	- QueryDSL

		- Type-safe가 가능함.
			- 감동의 컴파일 에러, 감동의 IDE 지원,
		- 동적 쿼리에 사용 용이
			- 동적쿼리, 복잡한 쿼리, 조인 쿼리, 페이징, 정렬에 모두 사용이 가능함
			- 쿼리에 들어가는 조건도 재사용이 가능하게 함수화 할 수 있음.

		- JpaItemRepositoryV3
			- EntityMapper : JpaItemRepository 때와 동일
			- JPAQueryFactory : JPAQueryFactory는 JPA 쿼리인 JPQL을 만들기 때문에 EntityManager가 필요

			- save() : JpaItemRepository 때와 동일
			- update() : JpaItemRepository 때와 동일
			- findById() : JpaItemRepository 때와 동일
			- findAll()
				- QueryDsl 사용
				- 일단 코드생성기(APT)를 통해서 QItem을 만들어야함.
				- query.select(item).from(item).where( likeItemName(itemName), maxPrice(maxPrice) ).fetch();
					- 쿼리문의 결과를 반환. 
					- likeItemName(itemName), maxPrice(maxPrice)을 사용해서 조건 부여.
					- fetch()를 사용해서 목록 조회

				- BooleanBuilder을 사용해서 where 조건을 넣어줄 수 있고 BooleanExpression을 반환해서 함수로 만들어 반환할 수 도 있음.
					- 함수로 반환 하는 경우
					- likeItemName(itemName) 호출
						- return item.itemName.like("%" + itemName + "%"); or return null;
					- maxPrice(maxPrice) 호출
						- return item.price.loe(maxPrice); or return null;

		- 이젠 쿼리 문장에 오타가 있어도 컴파일 시점에서 오류를 다 막을 수 있음.
		- 동적 쿼리 문장을 손쉽고 간단하고 눈에 확 띄게 작성할 수 있음.

	- v2 (실용적인 구조)
		- ItemServiceV2 :
			- 아래 두 repository 구현체에 의존. (인터페이스를 통해서 다형성을 이용하지 않음)
			- ItemQueryRepositoryV2
				- 동적 쿼리를 손쉽게 처리하기 위해 QueryDSL을 사용함.
			- ItemRepositoryV2
				- JpaRepository를 상속함.
				- Spring Data JPA의 기능으로 공통 메서드를 사용하는 부분
 */