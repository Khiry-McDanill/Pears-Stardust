package com.zipcode.stardust.controller;

import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.PostRepository;
import com.zipcode.stardust.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SearchController {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public SearchController(PostRepository postRepository,
                            UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false, defaultValue = "") String q,
                         Model model) {

        List<Post> posts = postRepository
                .findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(q, q);

        List<User> users = userRepository
                .findByUsernameContainingIgnoreCase(q);

        model.addAttribute("query", q);
        model.addAttribute("posts", posts);
        model.addAttribute("users", users);

        return "search";
    }
}