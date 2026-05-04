package com.example.boardlogin.mapper.support;

import com.example.boardlogin.mapper.domain.User;
import com.example.boardlogin.mapper.web.dto.RegisterForm;
import com.example.boardlogin.mapper.web.dto.UserProfileDto;

/**
 * DTO ↔ Entity 변환을 한 곳에 모읍니다. 서비스는 “유스케이스 흐름”만 담당합니다.
 */
public final class UserMapper {

    private UserMapper() {
    }

    /** 회원가입: 폼 → 신규 엔티티 (아직 영속화 전) */
    public static User toNewEntity(RegisterForm form) {
        return new User(form.getUsername(), form.getPassword(), form.getName());
    }

    /** 화면 응답: 엔티티 → 노출용 DTO (비밀번호 등 민감 필드 제외) */
    public static UserProfileDto toProfileDto(User user) {
        return new UserProfileDto(user.getUsername(), user.getName());
    }
}
