package com.example.join32.service;

import com.example.join32.domain.User;
import com.example.join32.dto.LoginRequest;
import com.example.join32.dto.UserRegisterRequest;
import com.example.join32.dto.UserResponse;
import com.example.join32.repository.UserRepository;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Optional<UserResponse> login(LoginRequest request) {
        return userRepository.findByUsername(request.getUsername().trim())
                .filter(u -> u.getPassword().equals(request.getPassword()))
                .map(UserResponse::from);
    }

    @Transactional
    public void register(UserRegisterRequest request) {
        String username = request.getUsername().trim();
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("이미 사용 중인 아이디입니다.");
        }
        userRepository.save(new User(username, request.getPassword(), request.getName()));
    }
}
