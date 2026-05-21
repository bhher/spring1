package com.example.roomfit.domain;

public enum CommunityBoardType {
	FREE("자유게시판"),
	QNA("질문게시판"),
	REVIEW("후기게시판");

	private final String label;

	CommunityBoardType(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
