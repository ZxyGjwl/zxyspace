package com.zxyspace.service.impl;

import com.zxyspace.dto.TagDto;
import com.zxyspace.exception.ResourceNotFoundException;
import com.zxyspace.model.Tag;
import com.zxyspace.repository.TagRepository;
import com.zxyspace.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public TagDto getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        return mapToTagDto(tag);
    }

    @Override
    public TagDto getTagByName(String name) {
        Tag tag = tagRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "name", name));
        return mapToTagDto(tag);
    }

    @Override
    public List<TagDto> getAllTags() {
        return tagRepository.findAll().stream()
                .map(this::mapToTagDto)
                .collect(Collectors.toList());
    }

    @Override
    public Set<TagDto> getTagsByIds(Set<Long> ids) {
        return tagRepository.findByIdIn(ids).stream()
                .map(this::mapToTagDto)
                .collect(Collectors.toSet());
    }

    @Override
    public List<TagDto> getTagsByNames(List<String> names) {
        return tagRepository.findByNameIn(names).stream()
                .map(this::mapToTagDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TagDto createTag(TagDto tagDto) {
        // 检查是否已存在同名标签
        if (tagRepository.existsByName(tagDto.getName())) {
            throw new IllegalArgumentException("Tag with name '" + tagDto.getName() + "' already exists");
        }
        
        Tag tag = Tag.builder()
                .name(tagDto.getName())
                .build();
        
        Tag savedTag = tagRepository.save(tag);
        return mapToTagDto(savedTag);
    }

    @Override
    @Transactional
    public TagDto updateTag(Long id, TagDto tagDto) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        
        // 如果更改了名称，需要检查是否与其他标签重名
        if (!tag.getName().equals(tagDto.getName()) && 
                tagRepository.existsByName(tagDto.getName())) {
            throw new IllegalArgumentException("Tag with name '" + tagDto.getName() + "' already exists");
        }
        
        tag.setName(tagDto.getName());
        
        Tag updatedTag = tagRepository.save(tag);
        return mapToTagDto(updatedTag);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));
        
        tagRepository.delete(tag);
    }

    @Override
    public boolean existsByName(String name) {
        return tagRepository.existsByName(name);
    }
    
    // 辅助方法：将Tag实体映射为TagDto
    private TagDto mapToTagDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
} 