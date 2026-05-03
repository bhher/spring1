package com.example.memberboard.domain.user.dto;

import com.example.memberboard.domain.user.User;
import java.time.LocalDateTime;
import lombok.Getter;

/**
 * 2단계(생성자): Entity → DTO 는 {@code new UserResponseDto(user)} 한 번에 복사합니다.
 */
@Getter
public class UserResponseDto {

	private final Long id;
	private final String email;
	private final String nickname;
	private final LocalDateTime createdAt;

	public UserResponseDto(User user) {
		this.id = user.getId();
		this.email = user.getEmail();
		this.nickname = user.getNickname();
		this.createdAt = user.getCreatedAt();
	}
}
