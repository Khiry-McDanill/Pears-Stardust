package com.zipcode.stardust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.UserRepository;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public String adminDashboard(Model model) {

        model.addAttribute(
                "users",
                userRepository.findAll());

        return "admin";
    }

    @PostMapping("/promote")
    public String promoteUser(@RequestParam Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        user.setAdmin(true);

        userRepository.save(user);

        return "redirect:/admin";
    }

    @PostMapping("/demote")
    public String demoteUser(@RequestParam Long userId) {

        User user = userRepository.findById(userId).orElseThrow();

        user.setAdmin(false);

        userRepository.save(user);

        return "redirect:/admin";
    }
}
