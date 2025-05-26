package com.zxyspace.service.impl;

import com.zxyspace.dto.CategoryDto;
import com.zxyspace.exception.ResourceNotFoundException;
import com.zxyspace.model.Category;
import com.zxyspace.repository.CategoryRepository;
import com.zxyspace.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return mapToCategoryDto(category);
    }

    @Override
    public CategoryDto getCategoryByName(String name) {
        Category category = categoryRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "name", name));
        return mapToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(this::mapToCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto categoryDto) {
        // 检查是否已存在同名分类
        if (categoryRepository.existsByName(categoryDto.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDto.getName() + "' already exists");
        }
        
        Category category = Category.builder()
                .name(categoryDto.getName())
                .description(categoryDto.getDescription())
                .build();
        
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryDto(savedCategory);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        // 如果更改了名称，需要检查是否与其他分类重名
        if (!category.getName().equals(categoryDto.getName()) && 
                categoryRepository.existsByName(categoryDto.getName())) {
            throw new IllegalArgumentException("Category with name '" + categoryDto.getName() + "' already exists");
        }
        
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        
        Category updatedCategory = categoryRepository.save(category);
        return mapToCategoryDto(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        categoryRepository.delete(category);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }
    
    // 辅助方法：将Category实体映射为CategoryDto
    private CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
} 