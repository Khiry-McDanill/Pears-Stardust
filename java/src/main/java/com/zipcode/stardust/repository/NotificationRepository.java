package com.zipcode.stardust.repository;

import com.zipcode.stardust.model.Notification;
import com.zipcode.stardust.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    List<Notification> findByRecipientAndReadFalseOrderByCreatedAtDesc(
            User recipient
    );
}