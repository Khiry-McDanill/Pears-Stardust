package com.zipcode.stardust.controller;

import com.zipcode.stardust.model.Notification;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.NotificationRepository;
import com.zipcode.stardust.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class NotificationController {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationController(
            NotificationRepository notificationRepository,
            UserRepository userRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/notifications")
    public String notifications(
            Authentication authentication,
            Model model) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        List<Notification> notifications =
                notificationRepository
                        .findByRecipientOrderByCreatedAtDesc(user);

        model.addAttribute("notifications", notifications);

        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        Notification notification =
                notificationRepository
                        .findById(id)
                        .orElseThrow();

        if (notification.getRecipient().getId()
                .equals(user.getId())) {

            notification.setRead(true);

            notificationRepository.save(notification);
        }

        return "redirect:/notifications";
    }
}