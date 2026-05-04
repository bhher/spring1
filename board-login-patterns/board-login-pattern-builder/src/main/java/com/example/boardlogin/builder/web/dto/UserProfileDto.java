package com.example.boardlogin.builder.web.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 프로필 응답 DTO. Lombok {@code @Builder} 로 불변 필드 조립.
 */
@Getter
@Builder
public class UserProfileDto {

    private final String username;
    private final String name;
}
