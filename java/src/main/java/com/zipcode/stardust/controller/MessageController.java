package com.zipcode.stardust.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zipcode.stardust.model.User;
import com.zipcode.stardust.service.DirectMessageService;
import com.zipcode.stardust.repository.UserRepository;

@Controller
public class MessageController {

    private final DirectMessageService directMessageService;
    private final UserRepository userRepository;

    public MessageController(
            DirectMessageService directMessageService,
            UserRepository userRepository) {

        this.directMessageService = directMessageService;
        this.userRepository = userRepository;
    }

    @GetMapping("/messages/{username}")
    public String viewConversation(
            @PathVariable String username,
            Authentication authentication,
            Model model) {

        User currentUser = userRepository.findByUsername(authentication.getName()).orElseThrow();
        User otherUser = userRepository.findByUsername(username).orElseThrow();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("otherUser", otherUser);
        model.addAttribute(
                "messages",
                directMessageService.getConversation(currentUser, otherUser)
        );

        return "messages";
    }

    @GetMapping("/messages")
    public String messagesHome(
            Authentication authentication,
            Model model) {

        User currentUser = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("users", userRepository.findAll());

        return "message-index";
    }

    @PostMapping("/messages/{username}")
    public String sendMessage(
            @PathVariable String username,
            @RequestParam String content,
            Authentication authentication) {

        User currentUser = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        User otherUser = userRepository
                .findByUsername(username)
                .orElseThrow();

        directMessageService.sendMessage(
                currentUser,
                otherUser,
                content
        );

        return "redirect:/messages/" + username;

    }
}
