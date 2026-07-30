package com.demo.demo.audit.service;

import com.demo.demo.audit.entity.AuditLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
public class AuditEmailService {

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM uuuu, HH:mm:ss");

    private final AuditLogService auditLogService;
    private final JavaMailSender mailSender;
    private final String fromAddress;

    public AuditEmailService(AuditLogService auditLogService,
                             JavaMailSender mailSender,
                             @Value("${audit.mail.from}") String fromAddress) {
        this.auditLogService = auditLogService;
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendLast24HourReport(String adminEmail) {
        List<AuditLog> auditLogs = auditLogService.findLast24Hours();

        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(adminEmail);
            helper.setSubject("Banking audit report - last 24 hours");
            helper.setText(buildHtmlReport(auditLogs), true);
            mailSender.send(message);

            log.info("Sent {}-entry audit report to {}", auditLogs.size(), adminEmail);
        } catch (Exception exception) {
            log.error("Unable to send audit report to {}", adminEmail, exception);
            throw new IllegalStateException("Unable to send audit report", exception);
        }
    }

    private String buildHtmlReport(List<AuditLog> auditLogs) {
        StringBuilder rows = new StringBuilder();
        for (AuditLog auditLog : auditLogs) {
            rows.append("<tr>")
                    .append(cell(auditLog.getOccurredAt().format(DATE_FORMAT)))
                    .append(cell(auditLog.getReferenceId()))
                    .append(cell(auditLog.getTransactionType().name()))
                    .append(cell(auditLog.getAccountNumber()))
                    .append(cell(auditLog.getCounterpartyAccountNumber()))
                    .append(cell(auditLog.getAmount()))
                    .append(cell(auditLog.getBalanceBefore()))
                    .append(cell(auditLog.getBalanceAfter()))
                    .append("</tr>");
        }

        if (auditLogs.isEmpty()) {
            rows.append("<tr><td colspan=\"8\" style=\"text-align:center\">"
                    + "No transaction audit records were created in the last 24 hours."
                    + "</td></tr>");
        }

        return """
                <!DOCTYPE html>
                <html><body style="font-family:Arial,sans-serif;color:#1f2937">
                <h2>Banking audit report</h2>
                <p>Transactions completed in the last 24 hours: <strong>%d</strong></p>
                <table style="border-collapse:collapse;width:100%%" border="1" cellpadding="8">
                  <thead style="background:#e5e7eb"><tr>
                    <th>Completed at</th><th>Reference</th><th>Type</th><th>Account</th>
                    <th>Counterparty</th><th>Amount</th><th>Balance before</th><th>Balance after</th>
                  </tr></thead><tbody>%s</tbody>
                </table>
                </body></html>
                """.formatted(auditLogs.size(), rows);
    }

    private String cell(Object value) {
        return "<td>" + escapeHtml(value == null ? "-" : value.toString()) + "</td>";
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
