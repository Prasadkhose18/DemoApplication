package com.demo.demo.audit.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditEmailRequestedEvent(
        UUID eventId,
        String adminEmail,
        LocalDateTime requestedAt) {
}
