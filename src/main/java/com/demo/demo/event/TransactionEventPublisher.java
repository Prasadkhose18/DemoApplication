package com.demo.demo.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public TransactionEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(TransactionCompletedEvent event) {
        applicationEventPublisher.publishEvent(event);
        log.info("Transaction completed event published. Reference: {}",
                event.referenceId());
    }
}
