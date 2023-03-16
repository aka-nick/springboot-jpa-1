package jpabook.jpashop;

import javax.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @Autowired
    private EntityManager em;

    public Long save(Member member) {
        em.persist(member);
        return member.getId();
        /*
        id만 반환하는 이유: 데이터 변경을 일으킨 경우, 변경한 데이터(== 입력한 데이터)를
        다시 변경하기 용이한 상태로 만들면(== 입력한 데이터를 다시 반환해서, save()를 호출한 쪽에서 다시 멤버 객체를 변경할 수 있도록 해주면)
        원치 않은 사이드 이펙트가 일어날 수 있다.
        이럴 때에는 입력에 대한 확인 정도만 가능하도록 ID를 반환하는게 적절할 수 있다.
         */
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }

}
