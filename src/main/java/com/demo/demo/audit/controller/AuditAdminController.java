package com.demo.demo.audit.controller;

import com.demo.demo.audit.dto.AuditEmailRequestResponse;
import com.demo.demo.audit.event.AuditEmailRequestedEvent;
import com.demo.demo.audit.producer.AuditEmailEventProducer;
import com.demo.demo.dto.response.ApiResponse;
import com.demo.demo.security.services.CurrentUserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/admin/audit")
@PreAuthorize("@currentUserService.isAdmin()")
public class AuditAdminController {

    private final AuditEmailEventProducer auditEmailEventProducer;
    private final CurrentUserService currentUserService;

    public AuditAdminController(AuditEmailEventProducer auditEmailEventProducer,
                                CurrentUserService currentUserService) {
        this.auditEmailEventProducer = auditEmailEventProducer;
        this.currentUserService = currentUserService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AuditEmailRequestResponse>> requestAuditEmail(
            HttpServletRequest request) {

        String adminEmail = currentUserService.getCurrentUserEmail();
        UUID requestId = UUID.randomUUID();

        auditEmailEventProducer.publish(new AuditEmailRequestedEvent(
                requestId,
                adminEmail,
                LocalDateTime.now()
        ));

        log.info("Admin {} requested audit email {}", adminEmail, requestId);

        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(ApiResponse.success(
                        HttpStatus.ACCEPTED,
                        "Audit email request accepted",
                        new AuditEmailRequestResponse(requestId, adminEmail),
                        request.getRequestURI()
                ));
    }
}
