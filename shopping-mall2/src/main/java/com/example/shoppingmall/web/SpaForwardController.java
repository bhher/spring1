package com.example.shoppingmall.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * React Router 경로는 서버에 템플릿이 없으므로 {@code index.html} 로 포워드합니다.
 */
@Controller
public class SpaForwardController {

	@RequestMapping(
			method = RequestMethod.GET,
			value = {
					"/",
					"/login",
					"/register",
					"/products",
					"/products/**",
					"/cart",
					"/cart/**",
					"/mypage/**",
					"/admin/**"
			})
	public String forward() {
		return "forward:/index.html";
	}
}
