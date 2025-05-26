package com.zxyspace.controller;

import com.zxyspace.dto.TagDto;
import com.zxyspace.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/tags")
public class TagController {

    private final TagService tagService;

    @Autowired
    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TagDto> getTagByName(@PathVariable String name) {
        return ResponseEntity.ok(tagService.getTagByName(name));
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/ids")
    public ResponseEntity<Set<TagDto>> getTagsByIds(@RequestParam Set<Long> ids) {
        return ResponseEntity.ok(tagService.getTagsByIds(ids));
    }

    @GetMapping("/names")
    public ResponseEntity<List<TagDto>> getTagsByNames(@RequestParam List<String> names) {
        return ResponseEntity.ok(tagService.getTagsByNames(names));
    }

    @PostMapping
    public ResponseEntity<TagDto> createTag(@RequestBody TagDto tagDto) {
        return new ResponseEntity<>(tagService.createTag(tagDto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagDto> updateTag(
            @PathVariable Long id, 
            @RequestBody TagDto tagDto) {
        return ResponseEntity.ok(tagService.updateTag(id, tagDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
} 