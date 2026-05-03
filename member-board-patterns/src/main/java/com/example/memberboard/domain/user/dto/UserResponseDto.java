package com.example.memberboard.domain.user.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

	private Long id;
	private String email;
	private String nickname;
	private LocalDateTime createdAt;
}
