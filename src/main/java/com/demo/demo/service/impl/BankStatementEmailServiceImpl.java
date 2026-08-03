package com.demo.demo.service.impl;

import com.demo.demo.dto.response.BankStatementResponseDTO;
import com.demo.demo.dto.response.TransactionStatementDTO;
import com.demo.demo.exception.StatementEmailDeliveryException;
import com.demo.demo.service.BankStatementEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class BankStatementEmailServiceImpl implements BankStatementEmailService {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM uuuu, HH:mm:ss");

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public BankStatementEmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${statement.mail.from:${spring.mail.username}}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendStatement(BankStatementResponseDTO statement,
                              String recipientEmail) {

        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(recipientEmail);
            helper.setSubject("Bank statement - " + statement.getAccountNumber());
            helper.setText(buildHtmlStatement(statement), true);
            mailSender.send(message);

            log.info("Bank statement sent successfully. Account: {}, Recipient: {}",
                    statement.getAccountNumber(), recipientEmail);
        } catch (Exception exception) {
            log.error("Unable to send bank statement. Account: {}, Recipient: {}",
                    statement.getAccountNumber(), recipientEmail, exception);
            throw new StatementEmailDeliveryException(
                    "Unable to send bank statement email", exception);
        }
    }

    private String buildHtmlStatement(BankStatementResponseDTO statement) {
        StringBuilder rows = new StringBuilder();
        for (TransactionStatementDTO transaction : statement.getTransactions()) {
            rows.append("<tr>")
                    .append(cell(transaction.getTransactionTime()
                            .format(DATE_TIME_FORMAT)))
                    .append(cell(transaction.getReferenceId()))
                    .append(cell(transaction.getDescription()))
                    .append(cell(transaction.getAmount()))
                    .append(cell(transaction.getBalanceBefore()))
                    .append(cell(transaction.getBalanceAfter()))
                    .append("</tr>");
        }

        if (statement.getTransactions().isEmpty()) {
            rows.append("<tr><td colspan=\"6\" style=\"text-align:center\">"
                    + "No transactions found for the requested period."
                    + "</td></tr>");
        }

        return """
                <!DOCTYPE html>
                <html><body style="font-family:Arial,sans-serif;color:#1f2937">
                <h2>Bank statement</h2>
                <p><strong>Account:</strong> %s<br>
                   <strong>Holder:</strong> %s<br>
                   <strong>Period:</strong> %s to %s<br>
                   <strong>Total transactions:</strong> %d</p>
                <table style="border-collapse:collapse;width:100%%" border="1" cellpadding="8">
                  <thead style="background:#e5e7eb"><tr>
                    <th>Time</th><th>Reference</th><th>Description</th><th>Amount</th>
                    <th>Balance before</th><th>Balance after</th>
                  </tr></thead><tbody>%s</tbody>
                </table>
                </body></html>
                """.formatted(
                escapeHtml(statement.getAccountNumber()),
                escapeHtml(statement.getHolderName()),
                statement.getFromDate(),
                statement.getToDate(),
                statement.getTotalTransactions(),
                rows);
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
