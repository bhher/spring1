package com.example.boardlogin.builder.service;

import com.example.boardlogin.builder.domain.User;
import com.example.boardlogin.builder.repository.UserRepository;
import com.example.boardlogin.builder.web.dto.RegisterForm;
import com.example.boardlogin.builder.web.dto.UserProfileDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        User user = User.builder()
                .username(form.getUsername())
                .password(form.getPassword())
                .name(form.getName())
                .build();
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserProfileDto loadProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
        return UserProfileDto.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }
}
