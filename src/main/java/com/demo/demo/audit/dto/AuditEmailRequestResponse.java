package com.demo.demo.audit.dto;

import java.util.UUID;

public record AuditEmailRequestResponse(UUID requestId, String recipient) {
}
