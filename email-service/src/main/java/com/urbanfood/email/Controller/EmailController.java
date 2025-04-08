package com.urbanfood.email.Controller;

import com.urbanfood.email.model.EmailNotification;
import com.urbanfood.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/send-notification")
    public ResponseEntity<String> sendNotification(@RequestBody EmailNotification notification) {
        log.info("Received request to send email notification: {}", notification);
        emailService.sendEmail(notification);
        return ResponseEntity.ok("Email notification queued successfully");
    }

    @GetMapping("/test/process-emails")
    public ResponseEntity<String> testProcessEmails() {
        log.info("Manually triggering email processing");
        emailService.processUnprocessedEmails(50);
        return ResponseEntity.ok("Email processing triggered");
    }
}