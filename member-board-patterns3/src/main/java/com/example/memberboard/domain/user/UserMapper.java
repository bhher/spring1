package com.example.memberboard.domain.user;

import com.example.memberboard.domain.user.dto.UserRegisterDto;
import com.example.memberboard.domain.user.dto.UserResponseDto;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

/**
 * 4단계(매퍼): DTO ↔ Entity 변환만 담당. Service 는 흐름·검증·트랜잭션에 집중합니다.
 */
@Component
public class UserMapper {

	public User toNewEntity(UserRegisterDto dto, String encodedPassword) {
		return new User(dto.getEmail(), encodedPassword, dto.getNickname(), LocalDateTime.now());
	}

	public UserResponseDto toResponseDto(User user) {
		return new UserResponseDto(user);
	}
}
