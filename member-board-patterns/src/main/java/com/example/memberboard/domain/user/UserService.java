package com.example.memberboard.domain.user;

import com.example.memberboard.domain.user.dto.UserLoginDto;
import com.example.memberboard.domain.user.dto.UserRegisterDto;
import com.example.memberboard.domain.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public void register(UserRegisterDto dto) {
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}

		User user = new User();
		user.setEmail(dto.getEmail());
		user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
		user.setNickname(dto.getNickname());
		user.setCreatedAt(LocalDateTime.now());

		userRepository.save(user);
	}

	public Long login(UserLoginDto dto) {
		User user = userRepository.findByEmail(dto.getEmail())
				.orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
		if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
			throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
		}
		return user.getId();
	}

	public void logout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
	}

	public UserResponseDto getProfile(Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
		return toResponseDto(user);
	}

	public User getEntity(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
	}

	private UserResponseDto toResponseDto(User user) {
		UserResponseDto dto = new UserResponseDto();
		dto.setId(user.getId());
		dto.setEmail(user.getEmail());
		dto.setNickname(user.getNickname());
		dto.setCreatedAt(user.getCreatedAt());
		return dto;
	}
}
