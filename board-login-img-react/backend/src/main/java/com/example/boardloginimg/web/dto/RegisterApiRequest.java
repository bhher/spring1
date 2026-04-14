package com.example.boardloginimg.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterApiRequest(
        @NotBlank @Size(max = 50) String username,
        @NotBlank @Size(max = 100) String password,
        @NotBlank @Size(max = 100) String passwordConfirm,
        @NotBlank @Size(max = 100) String name
) {
}
