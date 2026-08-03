package com.demo.demo.service;

import com.demo.demo.event.TransactionCompletedEvent;

public interface TransactionEmailService {

    void sendConfirmation(TransactionCompletedEvent event);
}
