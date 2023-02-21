package hello.jdbc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class JdbcApplication {

	public static void main(String[] args) {
		SpringApplication.run(JdbcApplication.class, args);
	}

}
/*
- JDBC

	- JDBC의 이해

		- 애플리케이션과 각각의 DB 사이의 공통적인 행위(커넥션 연결, SQL 전달, 결과 응답)를 추상화하여 표준으로 만든 인터페이스이다.
		- 특정 JDBC 드라이버를 통해서 구현체를 설정하고 JDBC 인터페이스의 메소드를 사용하면 DB의 종류에 상관없이 접근이 가능하다.
		- 애플리케이션은 특정 DB에 의존하지 않고 JDBC 표준 인터페이스에만 의존한다. (코드를 변경하지 않아도 된다.)
			- ex)
			- JDBC는 java.sql.Connection 표준 커넥션 인터페이스를 정의한다.
			- H2 데이터베이스는 위를 구현한 org.h2.jdbc.JdbcConnection 구현체를 제공한다.

	- JDBC와 최신데이터 접근 기술

		- JDBC 직접 사용
			- 애플리케이션 로직 -> (SQL) -> JDBC
		- SQL Mapper
			- 애플리케이션 로직 -> (SQL) -> SQL Mapper(jdbc Template, MyBatis) -> (SQL) -> JDBC
			- 장점 : JDBC를 편리하게 사용하도록 도와줌 (SQL 응답결과를 객체로, JDBC 반복코드 제거)
			- 단점 : SQL 직접 작성
		- ORM 기술
			- 애플리케이션 로직 -> (객체) -> JPA -> (SQL) -> JDBC
			- 장점 : 객체를 데이터베이스 테이블과 매핑
				- SQL 직접 작성 X, SQL 동적 작성, 데이터베이스 마다 다른 SQL 문법 해결
				- 개발 생산성이 높아짐
			- 단점 : 기술의 난이도가 높아 실무에서 사용하려면 깊이 학습해야한다.
			- 대표 기술 : 하이버네이트, 이클립스링크
		- 모든 기술들의 가장 마지막 절차가 JDBC이다. 따라서 JDBC의 원리를 알아두어야함

	- 데이터 베이스 연결

		- DriverManager는 라이브러리에 등록된 DB 드라이버들을 관리하고 커넥션을 획득하는 기능을 제공한다.
			- public static final String URL = "jdbc:h2:tcp://localhost/~/test";
			- DriverManager.getConnection(URL, USERNAME, PASSWORD)
				- DriverManager는 라이브러리에 등록된 드라이버 목록을 자동 인식.
				- 전체 DB 드라이버 각각에 특정 DB 드라이버 탐색 정보(URL)와, 접속에 필요한 정보(USERNAME, PASSWORD)를 넘겨서 커넥션을 획득할 수 있는지 확인.
					- 특정 드라이버가 드라이버 탐색 정보와 일치하지 않다면 처리할 수 없다는 결과를 보내고 다음 드라이버에 순서를 넘김.
				- 커넥션을 획득할 수 있다면 데이터베이스에 연결 => 커넥션을 획득 => 커넥션을 클라이언트에 반환.

	- JDBC 개발

		- 데이터베이스 테이블 생성, 도메인 객체 생성
		- 커넥션 연결 // ex) Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

		- SQL 전달 :
			- 생성 // ex) PrepareStatement pstmt = con.prepareStatement(sql);
			- SQL문 내부 '?'와 치환 // ex) pstmt.setString(1, "name");
			- SQL 전달 :
				- 등록, 수정, 삭제와 같은 테이블 쓰기 // ex) pstmt.excuteUpdate()
					- 변경된 행의 수를 반환
				- 조회와 같은 테이블 읽기 			// ex) pstmt.executeQuery()
					- SQL 쿼리에 맞게 조회된 테이블이 결과 집합(ResultSet)으로 반환.
			- PrepareStatement말고 Statement도 있다. 하지만 그것은 SQL injection 공격의 위험이 있다.
			- 그렇기 때문에 SQL 내부의 값과 동적으로 치환하는 쿼리가 있다면 PrepareStatement를 사용 권장한다.

		- 결과 응답 :
			- select 쿼리의 결과가 순서대로 들어간다.
			- 내부에 있는 커서(cursor)를 이동해서 다음 데이터를 조회 // ex) rs.next()
				- 최초의 커서는 데이터를 가리키고 있지 않아서 한번은 호출해야한다.
				- true면 값이 있음, false면 값이 없음.
			- 커서를 이동한 rs는 해당 행의 값을 가지고 있기 때문에 그 값을 추출할 수 있어야한다.
				- 현재 커서가 가리키고있는 행의 칼럼명을 가지고 그 값을 타입에 맞게 반환. // ex) rs.getString("member_id")

		- 리소스 정리 :
			- 예외가 터져도 해야하기 때문에 finally에 작성
			- 역순으로 실행 (ResultSet -> PrepareStatement -> Connection)
		
		- 등록, 조회, 수정, 삭제에 대해 디테일한 내용은 생략.

- 커넥션 풀, 데이터 소스

	- 커넥션을 새로 만드는 것은 과정도 복잡하고 시간도 많이 소모되는 일.
	- DB, 애플리케이션 서버에서도 TCP/IP 커넥션을 새로 생성하기 위해 리소스를 매번 사용.
	- 따라서 시간과 자원이 더 사용되어서 좋지 않은 사용 경험을 줄 수 있음.
	- 해당 문제를 해결하기 위해 커넥션 풀을 사용.
	
	- 커넥션 풀

		- 애플리케이션을 시작하는 시점에 커넥션 풀은 필요한 만큼 커넥션을 미리 확보해서 풀에 보관.
		- 해당 커넥션은 TCP/IP로 DB와 커넥션이 연결되어 있는 상태이기 때문에 즉시 SQL을 DB에 전달 가능.
		- 커넥션 조회(참조) -> 커넥션 획득 -> 커넥션 사용 -> 커넥션 풀에 반환(다시 사용).
		
		- 커넥션 풀의 커넥션의 수는 성능 테스트를 통해서 설정.
		- 커넥션 풀은 DB에 무한정 생성을 막아 DB를 보호하는 효과도 있음.
		- 실무에서는 항상 기본으로 사용
		- 주로 사용하는 커넥션 풀 오픈소스는 HikariCp임. (스프링 기본 제공)

	- DataSource

		- 커넥션 풀도 JDBC 인터페이스처럼 DataSource라는 인터페이스가 있고 여러 종류의 커넥션 풀 구현체가 있다.
		- 그렇기 때문에 각각의 커넥션 풀 오픈소스에 의존하지 않고 DataSource에만 의존하도록 애플리케이션 로직을 작성하면된다.
		- 커넥션 풀 구현 기술을 변경하고 싶으면 구현체를 바꿔 끼우면된다.

		- 앞서 배운 DriverManager는 커넥션 풀 오픈소스가 아니다. 그러나 DataSource를 통해 사용할 수 있도록 구현체를 만들어놓았다. (DriverManagerDataSource)
		- 기존 커넥션을 사용하다가 DataSource를 사용하고자 해도 애플리케이션 로직을 바꿀 필요가 없다.
		- DataSource는 DriverManager와 다르게 커넥션을 획득할 때마다 URL, USERNAME, PASSWORD와 같은 파라미터를 계속 전달하지 않아도 된다.
			- 설정과 사용의 분리.

		- 커넥션 풀
			- HikariCP 커넥션 풀 사용.
			- 커넥션 풀에서 커넥션을 생성하는 작업은 애플리케이션 실행 속도에 영향을 주지 않기 위해 별도의 쓰레드에서 작동
			- 커넥션 풀과 DriverManger 사용 비교
				- DriverManagerDataSource 사용시 conn0~5 번호를 통해서 항상 새로운 커넥션이 생성되어서 사용되는 것을 확인할 수 있다.
				- HikariDataSource 사용시 커넥션 풀 사용시 conn0 커넥션이 재사용된다.
					- 여러 요청이 동시에 들어오면 커넥션 풀의 다른 커넥션을 불러서 동작한다.
			- 또한 DriverManagerDataSource에서 HikariDataSource로 등록 설정을 변경해도 애플리케이션 로직 코드는 전혀 변경하지 않아도 된다. (DI + OCP)

		- JdbcUtils 편의 메서드
			- 스프링은 JDBC를 편리하게 다룰 수 있는 JdbcUtils를 제공
			- JdbcUtils를 사용하면 커넥션을 좀 더 편리하게 닫을 수 있음.

- 트랜잭션

	- 생략. pdf가 너무 잘되있음.

- 자바 예외 이해

	- 예외는 폭탄돌리기와 같다. 잡아서 처리하거나 처리할 수 없으면 밖으로 던져야함
	- 예외를 잡거나 던질때는 지정한 예외 외에 그 자식들도 함께 처리됨.

	- 체크 예외 : 컴파일러가 체크하는 예외 		// RuntimeException을 제외한 Exception 예외
		- 예외를 잡거나, 던져야한다.
		- 예외를 잡아서 처리하지 않으면 항상 throws에 던지는 예외를 선언해야함.
		- 장점 : 개발자의 실수를 컴파일러가 잡아주는 훌륭안 안전장치
		- 단점 : 모든 체크예외를 던저야하기 때문에 번거롭다. 그리고 의존 관계가 생기게 된다.

	- 언체크 예외 : 컴파일러가 체크하지 않는 예외	// RuntimeException 예외
		- 예외를 잡거나, 던지지 않아도 된다.
		- 예외를 잡아서 처리하지 않아도 throws를 생략할 수 있다. (선언 해도 됨)
			- 선언하면 개발자가 IDE를 통해서 좀더 편리하게 예외를 인지할 수 있음.
		- 장점 : 신경쓰고 싶지 않은 언체크 예외를 무시할 수 있음, 의존관계가 생기지 않음, throws 생략.
		- 단점 : 개발자가 실수로 예외를 누락할 수 있음.

	- 기본 원칙 :
		- 기본적으로 언체크(런타임)예외를 사용하자.
		- 체크 예외는 비즈니스 로직상 의도적으로 던지는 예외에만 사용하자.

	- 체크예외 활용
		- 문제점 :
			- 복구 불가능한 예외
				- 대부분 넘어오는 예외는 애플리케이션 로직에서 처리할 방법이 없다. => 일반적인 메시지를 보여준다. (로그처리)
			- 의존 관계에 대한 문제
				- OCP, DI를 통해 클라이언트 코드의 변경 없이 대상 구현체를 변경할 수 있다는 장점이 체크 예외 때문에 발목을 잡게 된다.

	- 언체크 예외 활용
		- 해결 : 예외 전환으로 해결 (체크 예외 -> 언체크 예외)
			- 예외를 공통으로 처리하는 부분을 마지막 타자가 처리하면 된다.
			- 예외를 전환할 때는 꼭 기존 예외인 스택트레이스를 포함해서 넘겨야한다.
				- 포함하지 않으면 기존 에외를 확인할 수 있는 방법이 없다.
			- 대신 문서화를 잘해야함.

			- 복구 불가능한 예외
				- 런타임 예외를 사용하면 서비스나 컨트롤러가 이런 복구 불가능한 예외를 신경쓰지 않아도 된다.
			- 의존 관계에 대한 문제
				- 언체크 예외는 throws를 생략할 수 있기 때문에 의존 관계가 부여되지 않는다.
				- 언체크 예외를 활용하면 인터페이스를 도입할 수 있다. (추상메소드의 throws 생략이 가능하기 때문)

	- 예외를 구분하여 동적 처리 (ex. 데이터베이스)
		- 예외 메시지의 데이터베이스 오류 코드를 사용하여 언체크 예외 활용
			=> 특정 데이터베이스에 의존된 코드가 됨.
	
	- 스프링 예외 추상화
		- 스프링은 데이터 접근 계층에 대한 수십가지 예외를 정리해서 일관된 예외 계층을 제공
		- 특정 기술에 종속적이지 않게 설계
		- 스프링이 제공하는 예외로 변환하는 역할도 제공

		- 스프링 데이터 접근 계층 예외 :
			- DataAccessException : 최상위 계층, RuntimeException을 상속
				- Transient : 일시적 오류, 다시 시도시 복구 가능성이 있음
					- ex) 쿼리 타임아웃, 락과 관련된 오류
				- NonTransient : 일시적이지 않은 오류, 다시 시도시 복구안됨
					- ex) SQL 문법 오류, 데이터베이스 제약조건 위배

		- 스프링 예외 변환기 :
			- SQLExceptionTranslator translator = new SQLErrorCodeSQLExceptionTranslator(dataSource);
			- translate("설명", sql, e) : 
				- 설명, 쿼리문, 예외(스택 트레이스)를 넣으면 동적으로 스프링 데이터 접근 계층의 예외로 변환하여 반환.
			- 각각의 DB가 제공하는 SQL 오류코드까지 고려해서 예외를 변환 => sql-error-codes.xml에 설정되어 있음
		
- 트랜잭션, 예외처리 버전업

	- V1 : 기본동작, 트랜잭션이 없어서 문제 발생
		- MemberRepositoryV1
		- MemberServiceV1
		- MemberServiceV1Test

		=> 트랜잭션을 수행하지 못함.

	- V2 : 트랜잭션 - 커넥션 파라미터 전달 방식 동기화
		- MemberRepositoryV2 : ConnectionParam 
			- findById(), update() 오버로딩
		- MemberServiceV2 : 트랜잭션 시작 - 파라미터 연동, 풀을 고려한 종료
			- 트랜잭션 시작(오토커밋 끔) -> 비즈니스 로직 -> commit() or rollback() -> release(오토커밋 킴 -> 커넥션 종료)
		- MemberServiceV2Test :

		=> 서비스 계층이 너무 지저분해짐, 커넥션 유지하도록 코드를 변경하는 것도 어려움
		
	- 문제 정리 :
		- 트랜잭션 문제
			- JDBC 구현 기술 서비스 계층 누수
			- 트랜잭션 동기화 문제
			- 트랜잭션 적용 반복 문제
		- 예외 누수 문제
		- 리포지토리 JDBC 반복 문제	=> DB 2편에서 해결

	- V3 : 스프링의 트랜잭션 추상화

		- V3_1 : 트랜잭션 매니저와 트랜잭션 동기화 매니저
			- MemberRepositoryV3 :
				- DataSourceUtils.getConnection()		// 트랜잭션 동기화에 사용
				- DataSourceUtils.releaseConnection() 	// 트랜잭션 동기화에 사용
				- 커넥션을 파라미터로 넘긴 오버로딩된 findById(), update() 사라짐
			- MemberServiceV3_1 :
				- PlatformTransactionManager 사용 => TransactionStatus 반환
			- MemberServiceV3Test_1
				- new DataSourceTransactionManager() => 구현체 (JDBC 트랜잭션 매니저) 주입
				- 데이터 소스 생성 -> 트랜잭션 매니저 생성 -> 사용

			=> JDBC 기술에 의존 문제 해결, 트랜잭션 동기화 문제 해결
			=> 에외 누수 아직 있음, 트랜잭션 적용 반복 문제 있음, 서비스 계층에 트랜잭션을 처리하는 기술 로직이 존재

		- V3_2 : 트랜잭션 템플릿
			- MemberRepositoryV3 :
				- V3_1의 내용과 같음
			- MemberServiceV3_2
				- TransactionTemplate는 transactionManager가 필요 => 생성자를 통해 주입 (Test 코드에서 주입)
				- txTemplate.executeWithourResult( (status) -> {} ); => 트랜잭션 시작 후 status의 상태에 따라서 커밋 or 롤백
			- MemberServiceV3Test_2
				- 내용은 V3_1과 같다.

			=> JDBC 기술에 의존 문제 해결, 트랜잭션 동기화 문제 해결, 트랜잭션 적용 반복 문제 해결
			=> 에외 누수 아직 있음, 서비스 계층에 트랜잭션을 처리하는 기술 로직이 존재

		- V3_3 : 트랜잭션 - @Transactional AOP
			- MemberRepositoryV3
				- V3_1의 내용과 같음
			- MemberServiceV3_3
				- 트랜잭션을 처리하는 기술 로직 모두 제거
				- 트랜잭션이 필요한 메소드 위에 @Transactional 애노테이션을 설정.
			- MemberServiceV3Test_3
				- @SpringBootTest으로 스프링 컨테이너 실행
				- @Autowired, @TestConfiguration으로 스프링 빈 등록

		- V3_4 : 스프링 부트의 자동 리소스 등록
			- MemberRepositoryV3
				- V3_1의 내용과 같음
			- MemberServiceV3_3
				- V3_3의 내용과 같음
			- MemberServiceV3Test_4
				- DataSource 스프링 빈 - 자동 등록 / HikariDataSource 사용됨, 설정 정보는 application.properties에 등록
				- 트랜잭션 매니저 - 자동등록 / 어떤 구현체를 선택할지는 등록된 라이브러리를 보고 판단,
				- 둘다 개발자가 직접 등록하면 스프링 부트는 자동으로 등록하지 않음

	- V4 : 예외 누수 문제 해결 

		- MemberRepository :
			- MemberRepositoryV4_1 : 런타임 예외 적용
				- throws 생략으로 인터페이스 도입이 가능 (DI를 사용해서 서비스 수정없이 구현 기술을 변경)
				- 예외 전환 : SQLException (체크) -> MyDbException (언체크)
				- 예외 전환시 꼭 스택 트레이스를 담을것
				- MyDbException : 직접 생성한 예외
				=> 특정 상황의 예외를 구분하여 대처할 수 없다.
			- MemberRepositoryV4_2 : 스프링 예외 추상화 적용 
				- SQLExceptionTranslator 추가
				- throw 시 translate() 메소드 사용해서 반환값으로 스프링 데이터 접근 계층 예외를 반환
		- MemberServiceV4
			- SQLException 모두 제거
			- MemberRepository 인터페이스에 의존
		- MemberServiceV4Test
			- SQLException 모두 제거
			- MemberRepository 인터페이스 사용
			- 해당 TestConfig에서 의존성 주입 (갈아 끼우기)
				- V3 -> V4_1 : 단순 예외 변환
				- V4_1 -> V4_2 : 스프링 예외 변환

	- V5 : JDBC 코드 반복 해결 

		- MemberRepository
			-MemberRepositoryV5 : JDBC Template 사용 
				- 템플릿 콜백 패턴을 이용하고 아래의 것들을 해줌
					- 반복 코드를 제거
					- 트랜잭션을 위한 커넥션 동기화
					- 스프링 예외 변환기 
				- JdbcTemplate 생성 => dataSource를 인자로 받음.
				- RowMapper에 대한 메소드를 만들어야함.
				- 자세한건 DB 2편에서 다룸.
		- MemberServiceV4
		- MemberServiceV4Test
			- MeberRepository 인터페이스의 구현체 변경
				- V4_2 -> V5 : JDBC 코드 반복 해결
			
 */