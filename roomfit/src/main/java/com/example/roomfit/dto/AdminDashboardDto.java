package com.example.roomfit.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardDto {

	private final long memberCount;
	private final long interiorPostCount;
	private final long communityPostCount;
	private final long pendingReportCount;
}
