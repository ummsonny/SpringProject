package jpabook.solo_jpashop.service;

import jpabook.solo_jpashop.domain.Member;
import jpabook.solo_jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)//joinjoin()메서드 제외(readOnly = false 이다)하고 모든 메서드에 적용됨
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional//변경 default가 false이므로 안적어줌
    public Long join(Member member){
        validateDuplicateMember(member);
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member){//회원이 이미 있는지 체크
        List<Member> findMembers =
                memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 전체회원조회
     */
    public List<Member> findMembers(){
        return memberRepository.findAll();
    }
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }
}
