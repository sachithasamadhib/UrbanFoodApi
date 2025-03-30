package com.urbanfood.email.repository;

import com.urbanfood.email.model.EmailNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailNotificationRepository extends JpaRepository<EmailNotification, Long> {

    @Query("SELECT e FROM EmailNotification e WHERE e.processed = 0 ORDER BY e.createdDate ASC")
    List<EmailNotification> findUnprocessedNotifications(org.springframework.data.domain.Pageable pageable);

    @Modifying
    @Query("UPDATE EmailNotification e SET e.processed = 1, e.processedDate = :processedDate WHERE e.id = :id")
    void markAsProcessed(@Param("id") Long id, @Param("processedDate") LocalDateTime processedDate);
}