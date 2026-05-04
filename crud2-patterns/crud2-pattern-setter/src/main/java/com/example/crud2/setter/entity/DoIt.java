package com.example.crud2.setter.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Setter 방식: 빈 객체 만든 뒤 setter로 채웁니다. */
@Entity
@Getter
@Setter
@NoArgsConstructor
public class DoIt {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long num;

	@Column
	private String title;

	@Column
	private String content;
}
