package com.example.imgboard.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostDto {

	private Long id;
	private String title;
	private String content;
	private List<MultipartFile> files;
}
