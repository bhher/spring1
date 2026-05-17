package com.example.roomfit.web;

import com.example.roomfit.domain.ReportStatus;
import com.example.roomfit.service.AdminService;
import com.example.roomfit.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

	private final AdminService adminService;
	private final ReportService reportService;

	@GetMapping
	public String dashboard(Model model) {
		model.addAttribute("stats", adminService.dashboard());
		model.addAttribute("reports", reportService.pendingReports());
		return "admin/dashboard";
	}

	@PostMapping("/reports/{id}")
	public String processReport(
			@PathVariable Long id,
			@RequestParam ReportStatus status,
			@RequestParam(required = false) String adminNote) {
		reportService.process(id, status, adminNote);
		return "redirect:/admin";
	}
}
