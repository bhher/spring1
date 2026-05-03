package com.example.memberboard.domain.user;

import com.example.memberboard.domain.user.dto.UserLoginDto;
import com.example.memberboard.domain.user.dto.UserRegisterDto;
import com.example.memberboard.domain.user.dto.UserResponseDto;
import jakarta.servlet.http.HttpSession;
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
	private final UserMapper userMapper;

	@Transactional
	public void register(UserRegisterDto dto) {
		if (userRepository.existsByEmail(dto.getEmail())) {
			throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
		}

		String encoded = passwordEncoder.encode(dto.getPassword());
		User user = userMapper.toNewEntity(dto, encoded);
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
		return userMapper.toResponseDto(user);
	}

	public User getEntity(Long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
	}
}
