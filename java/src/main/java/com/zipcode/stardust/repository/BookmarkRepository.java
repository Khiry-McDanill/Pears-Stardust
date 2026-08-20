package com.zipcode.stardust.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zipcode.stardust.model.Bookmark;
import com.zipcode.stardust.model.Post;
import com.zipcode.stardust.model.User;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByUserAndPost(User user, Post post);

    List<Bookmark> findByUserOrderByIdDesc(User user);
}