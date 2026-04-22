package member.member1.repository;

import member.member1.entity.Member;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends CrudRepository<Member,Long> {
    List<Member> findAll();

    Optional<Member> findByEmail(String email);
}
