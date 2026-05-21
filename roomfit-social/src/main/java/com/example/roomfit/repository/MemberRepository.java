package com.example.roomfit.repository;

import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.MemberStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByLoginId(String loginId);

	Optional<Member> findByEmail(String email);

	Optional<Member> findByOauthProviderAndOauthProviderSubject(String oauthProvider, String oauthProviderSubject);

	Optional<Member> findByNameAndEmail(String name, String email);

	boolean existsByLoginId(String loginId);

	boolean existsByEmail(String email);

	List<Member> findByStatus(MemberStatus status);

	long countByStatus(MemberStatus status);
}
