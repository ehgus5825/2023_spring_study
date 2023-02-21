package hello.servlet.web.frontcontroller;

import java.util.HashMap;
import java.util.Map;

// ModelView에서 하는 일
// 1. 뷰의 논리 이름을 담음
// 2. 모델을 지님


public class ModelView {
    // 뷰의 논리 이름
    private String viewName;
    // 모델
    private Map<String, Object> model = new HashMap<>();

    // 생성자 : 뷰의 논리 이름으로 생성
    public ModelView(String viewName) {
        this.viewName = viewName;
    }

    // 뷰의 논리 이름 반환
    public String getViewName() {
        return viewName;
    }

    // 뷰의 논리 이름 설정
    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    // 모델 반환
    public Map<String, Object> getModel() {
        return model;
    }

    // 모델 생성
    public void setModel(Map<String, Object> model) {
        this.model = model;
    }
}
