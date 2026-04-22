package member.member1.service;

import member.member1.dto.MemberDto;
import member.member1.entity.Member;
import member.member1.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    /**
     * 동일 이메일이 이미 저장되어 있으면 true.
     */
    public boolean isEmailRegistered(String email) {
        if (email == null || email.isBlank()) {
            return false;
        }
        return memberRepository.findByEmail(email.trim()).isPresent();
    }

    public void create(MemberDto dto) {
        Member member = new Member(dto);
        memberRepository.save(member);
    }

    public List<Member> findAll(){
        return memberRepository.findAll();
    }

    public Member findById(Long id) {
        return memberRepository.findById(id).orElse(null);
    }

    public void update(Long id, MemberDto dto) {
        Optional<Member> optionalMember = memberRepository.findById(id);
        //값이 존재 할 시
        if(optionalMember.isPresent()){
            Member member = optionalMember.get(); //optional을 get으로 꺼내올수 있음
            member.updateFromDto(dto);
            memberRepository.save(member);
        }
    }

    public void delete(Long id) {
        memberRepository.deleteById(id);
    }
}
