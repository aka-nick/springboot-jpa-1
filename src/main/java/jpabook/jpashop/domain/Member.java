package jpabook.jpashop.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;


/*
엔티티 설계 시 주의점
1. 세터 열지 말자 :
    - 변경포인트가 많아지는 건 좋지 않다. 진짜 필요한 곳에서만...도 아니다. 값을 변경해야 한다면 비즈니스 메서드를 먼저 고려하자.
2. 연관관계는 지연로딩으로 설정 :
    - 일단 지연로딩으로 해놓고 문제될 시 수정한다...가 아니라 일단 지연로딩을 쓰자. @ManyToOne, @OneToOne은 기본 전략이 즉시로딩이므로 수정해준다.
3. 컬렉션은 필드에서 바로 초기화 하자 :
    - null 문제에서 안전하다.
    - 하이버네이트는 엔티티를 영속화할 때 컬렉션을 감싸서 하이버네이트가 제공하는(하이버네이트가 추적할 수 있는) 내장 컬렉션으로 변경한다.
        - 그런데 내장 컬렉션으로 변경된 이후에 누군가가 다시 set~~~() 하거나, 새롭게 new 한다면? 하이버네이트는 해당 컬렉션을 추적할 수 없게되는, 메커니즘에 문제가 생긴다.
4. 컬럼명, 테이블명 설정 전 :
    - 하이버네이트 기본 전략도 괜찮다.
    - 논리명/물리명을 따로 설정할 수도 있다. SpringPhysicalNamingStrategy 구현체를 본인이 구현한 구현체로 갈아치우면 된다. 어지간하면 그냥 쓰자.
5. enum 사용 시 EnumType은 String으로 하자 :
    - @Enumerated(EnumType.STRING)
    - ORDINAL로 하는 것은 순서가 뒤바뀌면 장애가 난다.
6. 양방향 관계에는 연관 관계 편의 메서드가 필요하다
*/

@Entity
@Getter @Setter
public class Member {

    @Id
    @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    @NotEmpty
    private String name;

    @Embedded
    private Address address;

    @JsonIgnore
    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();

}


