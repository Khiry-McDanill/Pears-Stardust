package com.zipcode.stardust.service;

import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.PostLike;
import com.zipcode.stardust.model.User;
import com.zipcode.stardust.repository.PostLikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostLikeService {

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Transactional
    public boolean toggleLike(User user, Post post) {

        if (postLikeRepository.existsByUserAndPost(user, post)) {
            postLikeRepository.deleteByUserAndPost(user, post);
            return false;
        }

        PostLike postLike = new PostLike(user, post);
        postLikeRepository.save(postLike);

        return true;
    }

    public long getLikeCount(Post post) {
        return postLikeRepository.countByPost(post);
    }

    public boolean hasLiked(User user, Post post) {
        if (user == null) {
            return false;
        }

        return postLikeRepository.existsByUserAndPost(user, post);
    }
}