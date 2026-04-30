package com.example.boardlogin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.boardlogin.domain.User;
import com.example.boardlogin.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("로그인 성공 시 User 반환")
    void login_success() {
        User user = new User("id1", "pw1", "이름");
        when(userRepository.findByUsername("id1")).thenReturn(Optional.of(user));

        User result = userService.login("id1", "pw1");

        assertEquals(user, result);
    }

    @Test
    @DisplayName("비밀번호 불일치 시 null")
    void login_wrongPassword() {
        when(userRepository.findByUsername("id1"))
                .thenReturn(Optional.of(new User("id1", "right", "이름")));

        assertNull(userService.login("id1", "wrong"));
    }

    @Test
    @DisplayName("없는 아이디면 null")
    void login_notFound() {
        when(userRepository.findByUsername("none")).thenReturn(Optional.empty());

        assertNull(userService.login("none", "pw"));
    }

    @Test
    @DisplayName("findByUsername — 있으면 User, 없으면 null")
    void findByUsername() {
        User u = new User("a", "p", "n");
        when(userRepository.findByUsername("a")).thenReturn(Optional.of(u));
        when(userRepository.findByUsername("x")).thenReturn(Optional.empty());

        assertEquals(u, userService.findByUsername("a"));
        assertNull(userService.findByUsername("x"));
    }

    @Test
    @DisplayName("가입 — 아이디 중복이면 예외")
    void register_duplicate() {
        when(userRepository.existsByUsername("dup")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register("dup", "pw", "이름"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("가입 — 성공 시 save 호출")
    void register_ok() {
        when(userRepository.existsByUsername("newid")).thenReturn(false);

        userService.register("newid", "pw", "이름");

        verify(userRepository).save(any(User.class));
    }
}
