package com.example.shoppingmall.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 로컬 업로드 파일을 브라우저에서 {@code /uploads/**} URL 로 제공합니다.
 * 추후 S3 로 옮기면 이 매핑은 제거하고 CDN URL 을 쓰면 됩니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${app.upload-dir}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
		String location = root.toUri().toString();
		registry.addResourceHandler("/uploads/**").addResourceLocations(location + "/");
	}
}
