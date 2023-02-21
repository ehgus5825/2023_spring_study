package hello.upload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UploadApplication {

	public static void main(String[] args) {
		SpringApplication.run(UploadApplication.class, args);
	}

}
/*
-
- 파일 업로드

	- HTML 폼 전송 방식

		- application/x-www-form-urlencoded :
			- enctype 옵션이 없을 때 추가되는 전송 방식
			- 문자를 전송하는 방식

		- multipart/form-data :
			- enctype을 multipart/form-data로 지정
			- 문자 뿐만아니라 여러 파일과 폼의 내용을 함께 전송할 수 있음.
			- 전송되는 데이터가 각각 Part로 나누어져 있음.

	- 서블릿 파일업로드

		- 업로드 사이즈 제한 설정
			- application.properties에 설정
			- spring.servlet.multipart.max-file-size=1MB        // 파일 하나의 최대 사이즈, 가본 1MB
			- spring.servlet.multipart.max-request-size=10MB	// 여러 파일의 총 사이즈, 기본 10MB

		- spring.servlet.multipart.enabled 설정
			- application.properties에 해당 설정을 false로 하면 꺼지고, true로 하면 켜진다. (기본 true)
			- 해당 옵션을 끄면 서블릿 컨테이너는 멀티파트와 관련된 처리를 하지 않는다.
			- 옵션을 키면 스프링의 DispatcherServlet 에서 멀티파트 리졸버( MultipartResolver )를 실행한다.
			※ 하지만 실제로는 스프링의 MultipartFile를 사용하기 때문에 잘 사용하지 않음.

		- 파일이 저장될 경로를 설정하고 파일 업로드를 진행해야함.
			- application.properties에 file.dir 옵션에 파일 경로르 넣어주면 그 경로에 파일이 저장됨.
			- ex) filedir=/Users/kimyounghan/study/file/
			- 마지막 "/"가 중요함 하지 않으면 "file"과 파일 이름이 합쳐짐 ex) .../filetest

		- HTML 부분에서 form 태그에 enctype으로 multipart/form-data로 설정하고 데이터를 전송
		- 애노테이션으로 @Value("${file.dir}")를 작성하면 위의 application.properties에 지정된 file.dir에 해당하는 경로를 적용된 변수에 담는다.
		- servlet 부분에서 request.getPart()를 사용해서 Collection<Part>에 part들을 받음.

		- 그리고 받은 part들을 for 문으로 반복해서 값을 꺼내어 사용
			- 아래는 Part 인터페이스의 주요 메서드이다.
			- part.getSubmittedFileName() : 클라이언트가 전달한 파일명
			- part.getSize() : 파일의 사이즈
			- part.getInputStream(): Part의 전송 데이터를 읽을 수 있다.
			- part.write(Path): Part를 통해 전송된 데이터를 저장할 수 있다.  => 파일을 저장
				- 위의 fileDir과 part.getSubmittedFileName()을 합쳐서 전체 경로를 생성 (경로 + 파일이름)
				- 그리고 그 경로를 write() 메소드 내부에 담으면 파일이 해당 경로에 저장됨

	- 스프링과 파일 업로드

		- 스프링은 MultipartFile 이라는 인터페이스로 멀티파트 파일을 매우 편리하게 지원한다.
		- @RequestParam을 사용해서 컨트롤러의 인자로 바로 multipart 데이터를 바로 받을 수 있다. ( == request.getPart())

		- MultipartFile의 주요 메서드
			- multipartfile.isEmpty() : 파일이 비어있는지 확인
			- multipartfile.getOriginalFilename() : 업로드 파일명
			- multipartfile.transferTo(new File(path) : 파일 저장		=> 파일 저장
				- 내부에 File 클래스 인스턴스를 넣음.
				- File 클래스는 인자로 생성될 파일의 전체 경로를 넣어서 생성 (경로 + 파일이름)

	- 예제로 구현하는 파일 업로드, 다운로드

		- 이전 예제와 차이점 :
			- 여러개의 파일을 업로드
			- 업로드한 파일 다운로드
			- 업로드한 파일을 웹브라우저에서 확인

		- Item 클래스 (상품 도메인) :
			- id, 상품 이름, 첨부 파일, 이미지 파일 n개

		- ItemRepository 클래스 (상품 리포지토리) :
			- 상품 저장, 상품 찾기

		- UploadFile 클래스 (업로드 파일 정보 보관) :
			- 고객이 업로드한 파일명, 서버 내부에서 관리하는 파일명
			- 둘의 이름이 같으면 안됨 충돌이 일어날 수 있음.

		- FileStore 클래스 (파일 저장과 관련된 업무 처리) :
			- 파일 경로(file.dir)
			- 전체 경로 제작 메소드
			- 복수 파일 저장 메소드 => 반복문 속에서 단일 파일 저장 메소드를 호출  	// (List<MultipartFile> -> List<UploadFile>)
			- 단일 파일 저장 메소드 											// (MultiFile -> UploadFile)
			 	- 서버 내부 파일명 제작 메소드 호출해서 파일명을 만듬.
			 	- 파일명과 함께 전체 경로 제작 메소드를 호출해서 얻은 전체 경로로 파일을 저장. (DB가 아닌 디스크에 저장됨)
			 	- 서버 UploadFile 반환
			- 서버 내부 파일명 제작 메소드 => UUID(중복 X 식별자) + '.' + 확장자 추출 메소드 반환값
			- 확장자 추출 메소드 => '.' 이후의 확장자를 추출해서 반환

		- ItemForm 클래스 (HTML에서부터 받는 임시 폼)
			- [ItemForm -> Item] => [MultipartFile -> UploadFile]

		- ItemController
			- @GetMapping("/items/new") : 등록 폼을 보여준다.
				- item-form.html : 상품 입력 폼
					- 다중 파일 업로드를 하려면 input 태그에 multiple 옵션을 넣어주면 됨.
				
			- @PostMapping("/items/new") : 폼의 데이터를 저장하고 보여주는 화면으로 리다이렉트 한다.
				- 파일 저장소에 저장. 
				- 리포지토리에 상품 저장 (파일, 여러 이미지 파일)
				- 경로 변수를 만들어서 아래의 컨트롤러 호출
				
			- @GetMapping("/items/{id}") : 상품을 보여준다.
				- 경로 변수를 통해서 리포지토리에서 해당 상품을 꺼냄
				- 그것을 View를 통해서 보여줌
				- item-view.html : 특정 상품 뷰
					- 이미지 파일 : image 태그의 th:each 속성으로 여러 이미지 파일들을 화면에 보이게 함
					- 첨부 파일 : a 태그의 th:text로 ${item.getAttachFile().getUploadFileName()}를 넣어 업로드 파일 이름으로 출력함.
						- 링크를 누르면 아래의 @GetMapping("/images/{filename}")를 호출

			- @GetMapping("/images/{filename}") : <img> 태그로 이미지를 조회할 때 사용한다.
				- UrlResource로 이미지 파일을 읽어서 @ResponseBody 로 이미지 바이너리를 반환한다.
				- UrlResource 인스턴스 생성시 경로 앞에 "file:"를 추가해줘야한다.
				- UrlResource : 특정 URL로 정보를 읽어온다.

			- @GetMapping("/attach/{itemId}") : 파일을 다운로드 할 때 실행한다.
				- 예제를 더 단순화 할 수 있지만, 파일 다운로드 시 권한 체크같은 복잡한 상황까지 가정한다 생각하고 이미지 id 를 요청하도록 했다.
				- 파일 다운로드시에는 고객이 업로드한 파일 이름으로 다운로드 하는게 좋다.
				- 이때는 Content-Disposition 해더에 attachment; filename="업로드 파일명" 값을 주면 된다.
 */
