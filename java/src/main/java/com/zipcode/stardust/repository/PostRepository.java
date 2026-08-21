package com.zipcode.stardust.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.Subforum;
import com.zipcode.stardust.model.User;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findBySubforumOrderByPostdateDesc(Subforum subforum);

    List<Post> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(
            String title,
            String content
    );

    // Get the user's 5 most recent posts
    List<Post> findTop5ByUserOrderByPostdateDesc(User user);
}