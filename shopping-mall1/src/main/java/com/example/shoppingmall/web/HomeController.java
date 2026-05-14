package com.example.shoppingmall.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/** 메인 화면으로 이동합니다. */
@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "index";
	}
}
