package com.zxyspace.repository;

import com.zxyspace.model.Category;
import com.zxyspace.model.Post;
import com.zxyspace.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    Page<Post> findByPublishedTrue(Pageable pageable);
    
    Page<Post> findByAuthorAndPublishedTrue(User author, Pageable pageable);
    
    Page<Post> findByCategoryAndPublishedTrue(Category category, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.published = true AND " +
           "(LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Post> searchPosts(@Param("query") String query, Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(Pageable pageable);
    
    @Query("SELECT p FROM Post p WHERE p.published = true ORDER BY p.views DESC")
    List<Post> findPopularPosts(Pageable pageable);
}