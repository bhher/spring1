package com.example.roomfit.service;

import com.example.roomfit.domain.MemberStatus;
import com.example.roomfit.domain.PostStatus;
import com.example.roomfit.domain.ReportStatus;
import com.example.roomfit.dto.AdminDashboardDto;
import com.example.roomfit.repository.CommunityPostRepository;
import com.example.roomfit.repository.InteriorPostRepository;
import com.example.roomfit.repository.MemberRepository;
import com.example.roomfit.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

	private final MemberRepository memberRepository;
	private final InteriorPostRepository interiorPostRepository;
	private final CommunityPostRepository communityPostRepository;
	private final ReportRepository reportRepository;

	public AdminDashboardDto dashboard() {
		return AdminDashboardDto.builder()
				.memberCount(memberRepository.countByStatus(MemberStatus.ACTIVE))
				.interiorPostCount(interiorPostRepository.countByStatus(PostStatus.VISIBLE))
				.communityPostCount(communityPostRepository.countByStatus(PostStatus.VISIBLE))
				.pendingReportCount(reportRepository.countByStatus(ReportStatus.PENDING))
				.build();
	}
}
