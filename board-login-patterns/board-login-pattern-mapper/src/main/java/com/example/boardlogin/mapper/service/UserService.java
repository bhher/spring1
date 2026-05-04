package com.example.boardlogin.mapper.service;

import com.example.boardlogin.mapper.domain.User;
import com.example.boardlogin.mapper.repository.UserRepository;
import com.example.boardlogin.mapper.support.UserMapper;
import com.example.boardlogin.mapper.web.dto.RegisterForm;
import com.example.boardlogin.mapper.web.dto.UserProfileDto;
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
        userRepository.save(UserMapper.toNewEntity(form));
    }

    @Transactional(readOnly = true)
    public UserProfileDto loadProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원이 없습니다."));
        return UserMapper.toProfileDto(user);
    }
}
