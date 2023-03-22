package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    void 정상회원가입() {
        Member member = new Member();
        member.setName("홍길동");

        Long joinedMemberId = memberService.join(member);

        Assertions.assertThat(joinedMemberId)
                .isEqualTo(memberService.findOne(joinedMemberId).getId());
    }

    @Test
    void 중복회원가입불가() {
        Member member = new Member();
        member.setName("김길동");
        memberService.join(member);

        Member anotherMember = new Member();
        anotherMember.setName("김길동");

        Assertions.assertThatThrownBy(() -> memberService.join(anotherMember))
                .isInstanceOf(IllegalStateException.class);
    }
}