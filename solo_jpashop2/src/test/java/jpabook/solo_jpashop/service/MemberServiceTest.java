package jpabook.solo_jpashop.service;

import jpabook.solo_jpashop.domain.Member;
import jpabook.solo_jpashop.repository.MemberRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    MemberService memberService;

    //이게 주어졌을때(given) 이렇게 하면(when) 이렇게 한다 또는 검증해라(then)

    @Test
    //@Rollback(value = true)
    public void 회원가입() throws Exception{
        //given
        Member member = new Member();
        member.setName("형수");
        //when
        Long saveId = memberService.join(member);
        //then
        assertEquals(member, memberRepository.findOne(saveId));
    }

    @Test(expected = IllegalStateException.class)
    // 예외처리를 해준다. 이거 없애고 한 번 돌려방
    // 원래 memberService.join(member2)가 예외가 발생하니까 try-catch문으로 감싸야 하는데
    //이렇게 해결한다.
    public void 중복_회원_예약() throws Exception{
        //Given
        Member member1 = new Member();
        member1.setName("연주");
        Member member2 = new Member();
        member2.setName("연주");

        //when
        memberService.join(member1);
        memberService.join(member2);//예외가 발생한다.

        //Then
        fail("예외가 발생해야 한다.");
    }

}
