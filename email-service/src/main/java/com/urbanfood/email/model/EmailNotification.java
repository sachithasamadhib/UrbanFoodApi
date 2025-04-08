package com.urbanfood.email.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "email_notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailNotification {
    @Id
    @Column(name = "notification_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String email;

    private String firstname;

    private String lastname;

    @Column(nullable = false)
    private String subject;

    @Column(name = "notification_type", nullable = false)
    private String notificationType;

    @Column(name = "ProductID")
    private Long productId;

    @Column(name = "processed")
    private Integer processed;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;
}