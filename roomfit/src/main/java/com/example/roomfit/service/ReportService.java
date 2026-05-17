package com.example.roomfit.service;

import com.example.roomfit.domain.Member;
import com.example.roomfit.domain.Report;
import com.example.roomfit.domain.ReportStatus;
import com.example.roomfit.repository.ReportRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReportService {

	private final ReportRepository reportRepository;
	private final MemberService memberService;

	@Transactional
	public void report(Long reporterId, String targetType, Long targetId, String reason) {
		Member reporter = memberService.findById(reporterId);
		reportRepository.save(Report.builder()
				.reporter(reporter)
				.targetType(targetType)
				.targetId(targetId)
				.reason(reason)
				.build());
	}

	public List<Report> pendingReports() {
		return reportRepository.findByStatusOrderByCreatedAtDesc(ReportStatus.PENDING);
	}

	@Transactional
	public void process(Long reportId, ReportStatus status, String adminNote) {
		Report report = reportRepository.findById(reportId).orElseThrow();
		report.setStatus(status);
		report.setAdminNote(adminNote);
	}
}
