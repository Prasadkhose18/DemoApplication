package com.demo.demo.service;

import com.demo.demo.dto.response.DailyAuditReportDTO;

import java.time.LocalDate;

public interface AuditReportService {

    void sendAuditReportEmail();

    DailyAuditReportDTO generateDailyAuditReport();

    DailyAuditReportDTO generateAuditReport(
            LocalDate fromDate,
            LocalDate toDate
    );
}