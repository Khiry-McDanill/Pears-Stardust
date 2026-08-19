package com.zipcode.stardust.controller;

import com.zipcode.stardust.model.Bookmark;
import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.BookmarkRepository;
import com.zipcode.stardust.repository.PostRepository;
import com.zipcode.stardust.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class BookmarkController {

    private final BookmarkRepository bookmarkRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public BookmarkController(BookmarkRepository bookmarkRepository,
                              PostRepository postRepository,
                              UserRepository userRepository) {
        this.bookmarkRepository = bookmarkRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/bookmark")
    public String bookmark(@RequestParam Long post,
                           Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        Post postToBookmark = postRepository
                .findById(post)
                .orElseThrow();

        if (bookmarkRepository
                .findByUserAndPost(user, postToBookmark)
                .isEmpty()) {

            Bookmark bookmark = new Bookmark(user, postToBookmark);

            bookmarkRepository.save(bookmark);
        }

        return "redirect:/viewpost?post=" + post;
    }

    @PostMapping("/bookmark/remove")
    public String removeBookmark(@RequestParam Long post,
                                 Authentication authentication) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        Post postToRemove = postRepository
                .findById(post)
                .orElseThrow();

        bookmarkRepository
                .findByUserAndPost(user, postToRemove)
                .ifPresent(bookmarkRepository::delete);

        return "redirect:/viewpost?post=" + post;
    }

    @GetMapping("/bookmarks")
    public String bookmarks(Authentication authentication,
                            Model model) {

        User user = userRepository
                .findByUsername(authentication.getName())
                .orElseThrow();

        List<Bookmark> bookmarks =
                bookmarkRepository.findByUserOrderByIdDesc(user);

        model.addAttribute("bookmarks", bookmarks);

        return "bookmarks";
    }
}