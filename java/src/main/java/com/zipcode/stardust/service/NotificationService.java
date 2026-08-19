package com.zipcode.stardust.service;

import com.zipcode.stardust.model.Notification;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.NotificationRepository;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public void createNotification(User recipient,
                                   User sender,
                                   String message) {

        Notification notification =
                new Notification(recipient, sender, message);

        notificationRepository.save(notification);
    }
}