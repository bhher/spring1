package com.example.roomfit.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class OAuthUserProfile {

	private final String providerUserId;
	private final String email;
	private final String name;
	private final String nickname;
}
