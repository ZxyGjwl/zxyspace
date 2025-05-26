package com.zxyspace.service;

import com.zxyspace.dto.CategoryDto;

import java.util.List;

public interface CategoryService {
    
    CategoryDto getCategoryById(Long id);
    
    CategoryDto getCategoryByName(String name);
    
    List<CategoryDto> getAllCategories();
    
    CategoryDto createCategory(CategoryDto categoryDto);
    
    CategoryDto updateCategory(Long id, CategoryDto categoryDto);
    
    void deleteCategory(Long id);
    
    boolean existsByName(String name);
} 