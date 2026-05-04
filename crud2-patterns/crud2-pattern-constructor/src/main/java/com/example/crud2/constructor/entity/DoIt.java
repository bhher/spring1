package com.example.crud2.constructor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/** 생성자 방식: 필수 값은 생성자로 한 번에. */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DoIt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long num;

	@Column
	private String title;

	@Column
	private String content;

	public DoIt(Long num, String title, String content) {
		this.num = num;
		this.title = title;
		this.content = content;
	}
}
