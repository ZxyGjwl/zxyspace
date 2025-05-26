package com.zxyspace.service;

import com.zxyspace.dto.TagDto;

import java.util.List;
import java.util.Set;

public interface TagService {
    
    TagDto getTagById(Long id);
    
    TagDto getTagByName(String name);
    
    List<TagDto> getAllTags();
    
    Set<TagDto> getTagsByIds(Set<Long> ids);
    
    List<TagDto> getTagsByNames(List<String> names);
    
    TagDto createTag(TagDto tagDto);
    
    TagDto updateTag(Long id, TagDto tagDto);
    
    void deleteTag(Long id);
    
    boolean existsByName(String name);
} 