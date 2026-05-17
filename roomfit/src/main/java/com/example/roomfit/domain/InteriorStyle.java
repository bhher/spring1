package com.example.roomfit.domain;

public enum InteriorStyle {
	MINIMAL("미니멀"),
	SCANDINAVIAN("북유럽"),
	MODERN("모던"),
	EMOTIONAL("감성"),
	BUDGET("가성비"),
	BEGINNER("자취초보");

	private final String label;

	InteriorStyle(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
