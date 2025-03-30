package com.urbanfood.email.service;

import com.urbanfood.email.model.EmailNotification;

public interface EmailService {
    void sendEmail(EmailNotification notification);
    void processUnprocessedEmails(int batchSize);
}