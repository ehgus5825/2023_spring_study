package hello.hellospring.domain;

import jakarta.persistence.*;

@Entity // (jpa)
public class Member {

    // DB가 알아서 키를 생성해줌 (jpa)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column(name = "username")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
