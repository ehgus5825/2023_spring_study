package hello.login.web.session;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 세션 관리
 */
@Component                  // 스프링 빈 등록
public class SessionManager {

    /**
     * 쿠키의 key
     */
    public static final String SESSION_COOKIE_NAME = "mySessionId";
    
    /**
     * 세션 저장소
     */
    private Map<String, Object> sessionStore = new ConcurrentHashMap<>();   // 동시 요청에 안전

    /**
     * 세션에 데이터 등록
     *
     * @param response : 생성된 쿠키를 등록하는데 사용
     * @param value : 세션 ID와 함께 저장될 값
     */
    public void createSession(Object value, HttpServletResponse response){

        // 세션 id 생성 (임의의 추정 불가능한 랜덤 값 => UUID)
        String sessionId = UUID.randomUUID().toString();
        // 세션 저장소에 세션 ID와 보관할 값을 저장
        sessionStore.put(sessionId, value);     // value : Member
        // 세션 ID로 응답 쿠키를 생성해서 클라이언트에 전달
        response.addCookie(new Cookie(SESSION_COOKIE_NAME, sessionId));
    }

    /**
     * 세션에 데이터 조회
     *
     * @param request : request의 쿠키를 통해서 세션을 조회
     */
    public Object getSession(HttpServletRequest request){

        // 클라이언트가 요청한 세션 쿠키 값을 찾음
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);

        // 쿠키 값이 없다면 null을 반환 (세션이 없다!)
        if (sessionCookie == null){
            return null;
        }

        // 쿠키 값이 있다면 세션 저장소에 보관한 값을 조회해서 반환 (해당 세션이 있다!)
        return sessionStore.get(sessionCookie.getValue());
    }

    /**
     * 세션에 데이터 삭제 (만료)
     *
     * @param request : request의 쿠키를 통해서 세션을 만료
     */
    public void expire(HttpServletRequest request){

        // 클라이언트가 요청한 세션 쿠키 값을 찾음
        Cookie sessionCookie = findCookie(request, SESSION_COOKIE_NAME);

        // 쿠키 값이 있다면 세션 저장소에 보관한 세션 ID와 값을 제거
        if (sessionCookie != null) {
            sessionStore.remove(sessionCookie.getValue());
        }

        // 쿠키 값이 없다면 아무것도 하지 않음 => 쿠키에 없다면 세션에도 없음
    }

    /** 요청된 쿠키를 찾는 메소드
     *
     * @param request : request의 쿠키 배열을 조회해서
     * @param cookieName : 쿠키 이름에 부합하는 쿠키를 반환
     * 세션 조회, 세션 만료에서 사용
     * */
    public Cookie findCookie(HttpServletRequest request, String cookieName){

        // 쿠키에 값이 전혀 없다면 null을 반환
        if(request.getCookies() == null){
            return null;
        }

        // 쿠키 배열에서 cookieName과 부합하는 쿠키를 반환, 없다면 null을 반환
        return Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(cookieName))
                .findAny()
                .orElse(null);
    }
}
