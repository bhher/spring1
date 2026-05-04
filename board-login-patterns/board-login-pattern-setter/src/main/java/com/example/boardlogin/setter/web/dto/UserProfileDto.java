package com.example.boardlogin.setter.web.dto;

import lombok.Data;

/** 화면(Thymeleaf)에 넘기는 회원 요약 — Setter로 값 주입하는 예시 */
@Data
public class UserProfileDto {

    private String username;
    private String name;
}
