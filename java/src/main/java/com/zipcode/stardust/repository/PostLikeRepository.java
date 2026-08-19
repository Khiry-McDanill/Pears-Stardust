package com.zipcode.stardust.repository;

import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.PostLike;
import com.zipcode.stardust.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByUserAndPost(User user, Post post);

    @Transactional
    void deleteByUserAndPost(User user, Post post);

    long countByPost(Post post);
}