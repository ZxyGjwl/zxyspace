package com.zxyspace.repository;

import com.zxyspace.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    List<Tag> findByNameIn(List<String> names);
    
    Set<Tag> findByIdIn(Set<Long> ids);
    
    boolean existsByName(String name);
} 