package com.demo.demo.service.impl;

import com.demo.demo.event.TransactionCompletedEvent;
import com.demo.demo.service.TransactionEmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class TransactionEmailServiceImpl implements TransactionEmailService {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM uuuu, HH:mm:ss");

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public TransactionEmailServiceImpl(
            JavaMailSender mailSender,
            @Value("${transaction.mail.from:${spring.mail.username}}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    @Override
    public void sendConfirmation(TransactionCompletedEvent event) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(event.customerEmail());
            helper.setSubject("Transaction Successful - "
                    + displayTransactionType(event.transactionType()));
            helper.setText(buildEmailContent(event), true);
            mailSender.send(message);
        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Unable to send transaction confirmation email", exception);
        }
    }

    private String buildEmailContent(TransactionCompletedEvent event) {
        return """
                <!DOCTYPE html>
                <html><body style="font-family:Arial,sans-serif;color:#1f2937">
                <h2>Transaction successful</h2>
                <p>Hello %s,</p>
                <p>Your banking transaction has been completed successfully.</p>
                <table style="border-collapse:collapse" cellpadding="8" border="1">
                  <tr><th align="left">Transaction type</th><td>%s</td></tr>
                  <tr><th align="left">Amount</th><td>%s</td></tr>
                  <tr><th align="left">Account</th><td>%s</td></tr>
                  <tr><th align="left">Reference ID</th><td>%s</td></tr>
                  <tr><th align="left">Date and time</th><td>%s</td></tr>
                  <tr><th align="left">Updated balance</th><td>%s</td></tr>
                </table>
                <p>If you did not perform this transaction, please contact support immediately.</p>
                </body></html>
                """.formatted(
                escapeHtml(event.customerName()),
                escapeHtml(displayTransactionType(event.transactionType())),
                escapeHtml(event.amount().toString()),
                escapeHtml(maskAccountNumber(event.accountNumber())),
                escapeHtml(event.referenceId()),
                escapeHtml(event.transactionTime().format(DATE_TIME_FORMAT)),
                escapeHtml(event.balanceAfter().toString())
        );
    }

    private String displayTransactionType(com.demo.demo.enums.TransactionType type) {
        return switch (type) {
            case DEPOSIT -> "Deposit";
            case WITHDRAW -> "Withdrawal";
            case TRANSFER_DEBIT, TRANSFER_CREDIT -> "Transfer";
            case REVERSAL -> "Reversal";
        };
    }

    private String maskAccountNumber(String accountNumber) {
        int visibleCharacters = 4;
        if (accountNumber == null || accountNumber.length() <= visibleCharacters) {
            return "****";
        }
        return "*".repeat(accountNumber.length() - visibleCharacters)
                + accountNumber.substring(accountNumber.length() - visibleCharacters);
    }

    private String escapeHtml(String value) {
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
