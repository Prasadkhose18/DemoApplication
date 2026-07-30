package com.demo.demo.audit.producer;

import com.demo.demo.audit.event.TransactionCompletedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TransactionEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public TransactionEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishAfterCommit(TransactionCompletedEvent event) {
        applicationEventPublisher.publishEvent(event);
        log.debug("Transaction audit event registered for after-commit publishing: {}",
                event.referenceId());
    }
}
