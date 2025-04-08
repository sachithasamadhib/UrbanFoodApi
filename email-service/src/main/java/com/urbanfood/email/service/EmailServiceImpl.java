package com.urbanfood.email.service;

import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import com.urbanfood.email.model.EmailNotification;
import com.urbanfood.email.repository.EmailNotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final MailjetClient mailjetClient;
    private final EmailNotificationRepository emailNotificationRepository;
    private final TemplateEngine templateEngine;

    @Value("${mailjet.sender.email}")
    private String senderEmail;

    @Value("${mailjet.sender.name}")
    private String senderName;

    @Override
    @Transactional
    public void sendEmail(EmailNotification notification) {
        try {
            // Save notification to database if it's a new one
            if (notification.getId() == null) {
                notification.setId(emailNotificationRepository.getNextNotificationId());
                notification.setProcessed(0);
                if (notification.getCreatedDate() == null) {
                    notification.setCreatedDate(LocalDateTime.now());
                }
                emailNotificationRepository.save(notification);
                log.info("Saved new notification with ID: {}", notification.getId());
            }

            String htmlContent = generateEmailContent(notification);
            log.debug("Generated HTML content for email");

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", senderEmail)
                                            .put("Name", senderName))
                                    .put(Emailv31.Message.TO, new JSONArray()
                                            .put(new JSONObject()
                                                    .put("Email", notification.getEmail())
                                                    .put("Name", notification.getFirstname() + " " + notification.getLastname())))
                                    .put(Emailv31.Message.SUBJECT, notification.getSubject())
                                    .put(Emailv31.Message.HTMLPART, htmlContent)));

            log.info("Sending email to: {}", notification.getEmail());
            MailjetResponse response = mailjetClient.post(request);

            if (response.getStatus() == 200) {
                log.info("Email sent successfully to {}", notification.getEmail());
                markNotificationAsProcessed(notification.getId());
            } else {
                log.error("Failed to send email: {}", response.getData());
            }
        } catch (Exception e) {
            log.error("Error sending email", e);
        }
    }

    @Override
    @Transactional
    public void processUnprocessedEmails(int batchSize) {
        List<EmailNotification> notifications = emailNotificationRepository.findUnprocessedNotifications(
                PageRequest.of(0, batchSize));

        log.info("Processing {} unprocessed email notifications", notifications.size());

        for (EmailNotification notification : notifications) {
            sendEmail(notification);
        }
    }

    @Transactional
    private void markNotificationAsProcessed(Long id) {
        emailNotificationRepository.markAsProcessed(id, LocalDateTime.now());
        log.info("Marked notification {} as processed", id);
    }

    private String generateEmailContent(EmailNotification notification) {
        Context context = new Context();
        Map<String, Object> variables = new HashMap<>();

        variables.put("firstName", notification.getFirstname());
        variables.put("lastName", notification.getLastname());
        variables.put("productId", notification.getProductId());

        context.setVariables(variables);

        String template;
        switch (notification.getNotificationType()) {
            case "CART_ITEM_EXPIRED":
                template = "cart-item-expired";
                break;
            case "CART_ITEM_REMOVED":
                template = "cart-item-removed";
                break;
            default:
                template = "generic-notification";
                break;
        }

        log.debug("Using template: {} for notification type: {}", template, notification.getNotificationType());
        return templateEngine.process(template, context);
    }
}

