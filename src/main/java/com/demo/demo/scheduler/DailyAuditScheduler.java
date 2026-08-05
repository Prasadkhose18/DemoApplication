package com.demo.demo.scheduler;

import com.demo.demo.dto.response.DailyAuditReportDTO;
import com.demo.demo.service.AuditReportService;
import com.demo.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyAuditScheduler {

    private final AuditReportService auditReportService;
    private final EmailService emailService;

    @Value("${audit.admin.email}")
    private String auditAdminEmail;

    @Scheduled(cron = "0 0 * * * *")
    public void sendDailyAuditReport() {

        log.info("Starting Daily Audit Scheduler...");

        LocalDate yesterday = LocalDate.now().minusDays(1);

        DailyAuditReportDTO report =
                auditReportService.generateAuditReport(
                        yesterday,
                        yesterday
                );

        String subject = "Daily Audit Report - " + yesterday;
        String body = formatReportAsEmailBody(report);
        emailService.sendEmail(auditAdminEmail, subject, body);

        log.info("Daily Audit Report sent successfully.");
    }

    private String formatReportAsEmailBody(DailyAuditReportDTO report) {
        return String.format("""
                Daily Audit Report
                ==================
                Report Date: %s to %s
                Generated At: %s
                
                Transaction Summary:
                - Total Transactions: %d
                - Total Deposits: %d
                - Total Withdrawals: %d
                - Total Transfers: %d
                
                Amount Summary:
                - Total Deposit Amount: %s
                - Total Withdrawal Amount: %s
                - Total Transfer Amount: %s
                """,
                report.getFromDate(),
                report.getToDate(),
                report.getGeneratedAt(),
                report.getTotalTransactions(),
                report.getTotalDeposits(),
                report.getTotalWithdrawals(),
                report.getTotalTransfers(),
                report.getTotalDepositAmount(),
                report.getTotalWithdrawalAmount(),
                report.getTotalTransferAmount()
        );
    }
}