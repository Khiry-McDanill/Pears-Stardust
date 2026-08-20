package com.zipcode.stardust.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.CommentRepository;
import com.zipcode.stardust.repository.PostRepository;
import com.zipcode.stardust.repository.UserRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @GetMapping
    public String adminDashboard(Model model) {

        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("posts", postRepository.findAll());
        model.addAttribute("comments", commentRepository.findAll());

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

    @PostMapping("/delete-post")
    public String deletePost(@RequestParam Long postId) {

        postRepository.deleteById(postId);

        return "redirect:/admin";
    }

    @PostMapping("/delete-comment")
    public String deleteComment(@RequestParam Long commentId) {

        commentRepository.deleteById(commentId);

        return "redirect:/admin";
    }
}
