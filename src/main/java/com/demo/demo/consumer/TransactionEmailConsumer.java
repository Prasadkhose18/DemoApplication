package com.demo.demo.consumer;

import com.demo.demo.event.TransactionCompletedEvent;
import com.demo.demo.service.TransactionEmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionEmailConsumer {

    private final ObjectMapper objectMapper;
    private final TransactionEmailService transactionEmailService;

    public TransactionEmailConsumer(ObjectMapper objectMapper,
                                    TransactionEmailService transactionEmailService) {
        this.objectMapper = objectMapper;
        this.transactionEmailService = transactionEmailService;
    }

    @KafkaListener(topics = "${transaction.notification.topic}",
            groupId = "${transaction.notification.group-id}")
    public void consume(String payload) {
        try {
            TransactionCompletedEvent event = objectMapper.readValue(
                    payload, TransactionCompletedEvent.class);

            log.info("Transaction email sending started. Reference: {}",
                    event.referenceId());
            transactionEmailService.sendConfirmation(event);
            log.info("Transaction email sent successfully. Reference: {}",
                    event.referenceId());
        } catch (Exception exception) {
            log.error("Transaction email sending failed for notification: {}",
                    payload, exception);
            throw new IllegalStateException(
                    "Unable to process transaction email notification", exception);
        }
    }
}
