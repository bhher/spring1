package com.example.crud2.mapper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** DTO → 엔티티 변환은 {@link com.example.crud2.mapper.support.DoMapper} 에만 둡니다. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DoDto {

	private Long num;
	private String title;
	private String content;
}
