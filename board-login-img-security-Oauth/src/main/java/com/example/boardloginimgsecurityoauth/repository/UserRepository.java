package com.example.boardloginimgsecurityoauth.repository;

import com.example.boardloginimgsecurityoauth.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByOauthProviderAndOauthProviderSubject(String oauthProvider, String oauthProviderSubject);

    Optional<User> findByEmail(String email);
}
