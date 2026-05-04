package com.example.boardlogin.setter.service;

import com.example.boardlogin.setter.domain.User;
import com.example.boardlogin.setter.repository.UserRepository;
import com.example.boardlogin.setter.web.dto.RegisterForm;
import com.example.boardlogin.setter.web.dto.UserProfileDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Setter 방식: {@code new User()} 후 필드마다 setter 호출로 조립합니다.
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
        String username = form.getUsername();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(form.getPassword());
        user.setName(form.getName());
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDto loadProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
        UserProfileDto dto = new UserProfileDto();
        dto.setUsername(user.getUsername());
        dto.setName(user.getName());
        return dto;
    }
}
