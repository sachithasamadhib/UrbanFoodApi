package com.urbanfood.email.scheduler;

import com.urbanfood.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailScheduler {

    private final EmailService emailService;

    @Value("${email.scheduler.batch-size}")
    private int batchSize;

    @Scheduled(fixedRateString = "${email.scheduler.fixed-rate}")
    public void processEmails() {
        log.info("Starting scheduled email processing");
        emailService.processUnprocessedEmails(batchSize);
    }
}