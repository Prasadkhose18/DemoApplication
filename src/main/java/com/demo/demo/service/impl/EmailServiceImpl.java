package com.demo.demo.service.impl;

import com.demo.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${audit.mail.from}")
    private String fromEmail;

    @Override
    public void sendEmail(
            String to,
            String subject,
            String body
    ) {

        try {

            SimpleMailMessage message = new SimpleMailMessage();

            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            javaMailSender.send(message);

            log.info(
                    "Email sent successfully to {}",
                    to
            );

        } catch (Exception ex) {

            log.error(
                    "Failed to send email to {}",
                    to,
                    ex
            );

            throw new RuntimeException(
                    "Unable to send email.",
                    ex
            );
        }
    }

    @Override
    public void sendHtmlEmail(
            String to,
            String subject,
            String htmlContent
    ) {

        try {

            MimeMessage mimeMessage =
                    javaMailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mimeMessage,
                            true,
                            "UTF-8"
                    );

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);

            // true = HTML content
            helper.setText(htmlContent, true);

            javaMailSender.send(mimeMessage);

            log.info(
                    "HTML email sent successfully to {}",
                    to
            );

        } catch (MessagingException ex) {

            log.error(
                    "Failed to send HTML email to {}",
                    to,
                    ex
            );

            throw new RuntimeException(
                    "Unable to send HTML email.",
                    ex
            );
        }
    }
}