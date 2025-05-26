package com.zxyspace.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
    
    // 手动添加setter和getter方法，解决Lombok可能的编译问题
    public void setContent(List<T> content) {
        this.content = content;
    }
    
    public List<T> getContent() {
        return this.content;
    }
    
    public void setPage(int page) {
        this.page = page;
    }
    
    public int getPage() {
        return this.page;
    }
    
    public void setSize(int size) {
        this.size = size;
    }
    
    public int getSize() {
        return this.size;
    }
    
    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
    
    public long getTotalElements() {
        return this.totalElements;
    }
    
    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
    
    public int getTotalPages() {
        return this.totalPages;
    }
    
    public void setLast(boolean last) {
        this.last = last;
    }
    
    public boolean isLast() {
        return this.last;
    }
    
    public static <T> PageResponse<T> of(List<T> content, int page, int size, long totalElements, int totalPages, boolean last) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(content);
        response.setPage(page);
        response.setSize(size);
        response.setTotalElements(totalElements);
        response.setTotalPages(totalPages);
        response.setLast(last);
        return response;
    }
} 