package com.example.boardlogin.constructor.service;

import com.example.boardlogin.constructor.domain.User;
import com.example.boardlogin.constructor.repository.UserRepository;
import com.example.boardlogin.constructor.web.dto.RegisterForm;
import com.example.boardlogin.constructor.web.dto.UserProfileDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 생성자 방식: DTO에서 꺼낸 값으로 {@code new User(...)} 한 번에 생성합니다.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User login(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(password))
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Transactional
    public void register(RegisterForm form) {
        if (userRepository.existsByUsername(form.getUsername())) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        userRepository.save(new User(form.getUsername(), form.getPassword(), form.getName()));
    }

    @Transactional(readOnly = true)
    public UserProfileDto loadProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
        return new UserProfileDto(user.getUsername(), user.getName());
    }
}
