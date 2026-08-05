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

    /**
     * Runs every day at midnight.
     */
    @Scheduled(cron = "0 0 * * * *")
    public void sendDailyAuditReport() {

        log.info("Starting daily audit report generation.");

        LocalDate yesterday = LocalDate.now().minusDays(1);

        DailyAuditReportDTO report =
                auditReportService.generateAuditReport(
                        yesterday,
                        yesterday
                );

        String html =
                auditEmailBuilder.buildDailyAuditEmail(report);

        emailService.sendHtmlEmail(
                adminEmail,
                "Daily Banking Audit Report - " + yesterday,
                html
        );

        log.info("Daily audit report sent successfully.");

    }

}