package com.example.shoppingmall.web.api;

import com.example.shoppingmall.domain.Member;
import com.example.shoppingmall.dto.MemberMeDto;
import com.example.shoppingmall.dto.MemberRegisterDto;
import com.example.shoppingmall.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ApiAuthController {

	private final MemberService memberService;

	@GetMapping("/csrf")
	public Map<String, String> csrf(HttpServletRequest request) {
		CsrfToken token = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		if (token == null) {
			throw new IllegalStateException("CSRF token not available");
		}
		return Map.of(
				"headerName", token.getHeaderName(),
				"parameterName", token.getParameterName(),
				"token", token.getToken());
	}

	@GetMapping("/me")
	public MemberMeDto me(@AuthenticationPrincipal Member member) {
		return MemberMeDto.from(member);
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, String>> register(@Valid @RequestBody MemberRegisterDto dto) {
		memberService.register(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "회원가입이 완료되었습니다."));
	}
}
