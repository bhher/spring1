package com.example.roomfit.repository;

import com.example.roomfit.domain.Report;
import com.example.roomfit.domain.ReportStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {

	List<Report> findByStatusOrderByCreatedAtDesc(ReportStatus status);

	long countByStatus(ReportStatus status);
}
