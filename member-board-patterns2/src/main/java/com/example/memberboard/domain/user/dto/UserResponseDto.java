package com.example.memberboard.domain.user.dto;

import com.example.memberboard.domain.user.User;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 3단계(빌더): 정적 팩토리 {@link #from(User)} 안에서 Builder 로 DTO를 조립합니다.
 */
@Getter
@Builder
public class UserResponseDto {

	private final Long id;
	private final String email;
	private final String nickname;
	private final LocalDateTime createdAt;

	public static UserResponseDto from(User user) {
		return UserResponseDto.builder()
				.id(user.getId())
				.email(user.getEmail())
				.nickname(user.getNickname())
				.createdAt(user.getCreatedAt())
				.build();
	}
}
