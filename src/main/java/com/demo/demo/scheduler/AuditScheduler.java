package com.demo.demo.scheduler;

import com.demo.demo.dto.response.DailyAuditReportDTO;
import com.demo.demo.service.AuditReportService;
import com.demo.demo.service.EmailService;
import com.demo.demo.util.AuditEmailBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuditScheduler {

    private final AuditReportService auditReportService;
    private final AuditEmailBuilder auditEmailBuilder;
    private final EmailService emailService;

    @Value("${audit.admin.email}")
    private String adminEmail;

    @Scheduled(cron = "0 0 * * * *")
    public void sendHourlyAuditReport() {

        log.info("Starting hourly audit report generation.");

        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDate today = LocalDate.now();

        DailyAuditReportDTO report =
                auditReportService.generateAuditReport(yesterday, today);

        String html = auditEmailBuilder.buildDailyAuditEmail(report);

        emailService.sendHtmlEmail(
                adminEmail,
                "Hourly Banking Audit Report - Last 24 Hours",
                html
        );

        log.info("Hourly audit report sent successfully.");
    }
}