package com.example.boardlogin.constructor.web.dto;

import lombok.Getter;

/** 응답 DTO — 생성자로 필수 필드를 한 번에 채워 반쯤 불변 객체처럼 사용 */
@Getter
public class UserProfileDto {

    private final String username;
    private final String name;

    public UserProfileDto(String username, String name) {
        this.username = username;
        this.name = name;
    }
}
