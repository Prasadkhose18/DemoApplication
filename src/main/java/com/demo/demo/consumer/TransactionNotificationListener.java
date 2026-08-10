package com.demo.demo.consumer;

import com.demo.demo.event.TransactionCompletedEvent;
import com.demo.demo.service.TransactionEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionNotificationListener {

    private final TransactionEmailService transactionEmailService;

    public TransactionNotificationListener(TransactionEmailService transactionEmailService) {
        this.transactionEmailService = transactionEmailService;
    }

    @KafkaListener(
            topics = "${transaction.notification.topic}",
            groupId = "${transaction.notification.group-id}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleTransactionNotification(TransactionCompletedEvent event) {
        try {
            log.info("Consuming transaction notification. Reference: {}", event.referenceId());
            transactionEmailService.sendConfirmation(event);
            log.info("Transaction confirmation email sent. Reference: {}", event.referenceId());
        } catch (Exception exception) {
            log.error("Failed to send transaction confirmation email. Reference: {}",
                    event.referenceId(), exception);
        }
    }
}
