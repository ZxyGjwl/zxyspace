package com.zxyspace.repository;

import com.zxyspace.model.Comment;
import com.zxyspace.model.Post;
import com.zxyspace.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    List<Comment> findByPost(Post post);
    
    Page<Comment> findByPost(Post post, Pageable pageable);
    
    Page<Comment> findByUser(User user, Pageable pageable);
    
    int countByPost(Post post);
} 