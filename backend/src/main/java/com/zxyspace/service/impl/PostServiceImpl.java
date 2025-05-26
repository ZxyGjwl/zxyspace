package com.zxyspace.service.impl;

import com.zxyspace.dto.CategoryDto;
import com.zxyspace.dto.PageResponse;
import com.zxyspace.dto.PostCreateRequest;
import com.zxyspace.dto.PostDto;
import com.zxyspace.dto.PostFilter;
import com.zxyspace.dto.PostSummaryDto;
import com.zxyspace.dto.PostUpdateRequest;
import com.zxyspace.dto.TagDto;
import com.zxyspace.dto.UserSummaryDto;
import com.zxyspace.exception.ResourceNotFoundException;
import com.zxyspace.model.Category;
import com.zxyspace.model.Post;
import com.zxyspace.model.Tag;
import com.zxyspace.model.User;
import com.zxyspace.repository.CategoryRepository;
import com.zxyspace.repository.CommentRepository;
import com.zxyspace.repository.PostRepository;
import com.zxyspace.repository.TagRepository;
import com.zxyspace.repository.UserRepository;
import com.zxyspace.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository, 
                          UserRepository userRepository,
                          CategoryRepository categoryRepository,
                          TagRepository tagRepository,
                          CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
    }

    @Override
    public PostDto getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        // 增加浏览量
        post.incrementViews();
        postRepository.save(post);
        
        return mapToPostDto(post);
    }

    @Override
    public PageResponse<PostSummaryDto> getAllPosts(PostFilter filter) {
        Pageable pageable = getPageable(filter);
        
        Page<Post> postsPage;
        if (filter.getPublished() != null) {
            postsPage = postRepository.findByPublishedTrue(pageable);
        } else {
            postsPage = postRepository.findAll(pageable);
        }
        
        List<PostSummaryDto> content = postsPage.getContent().stream()
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                content,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }

    @Override
    public PageResponse<PostSummaryDto> getPostsByAuthor(Long authorId, PostFilter filter) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));
        
        Pageable pageable = getPageable(filter);
        
        Page<Post> postsPage = postRepository.findByAuthorAndPublishedTrue(author, pageable);
        
        List<PostSummaryDto> content = postsPage.getContent().stream()
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                content,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }

    @Override
    public PageResponse<PostSummaryDto> getPostsByCategory(Long categoryId, PostFilter filter) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryId));
        
        Pageable pageable = getPageable(filter);
        
        Page<Post> postsPage = postRepository.findByCategoryAndPublishedTrue(category, pageable);
        
        List<PostSummaryDto> content = postsPage.getContent().stream()
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                content,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }

    @Override
    public PageResponse<PostSummaryDto> getPostsByTag(Long tagId, PostFilter filter) {
        // 由于没有直接的Repository方法，我们需要手动实现此功能
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));
        
        List<Post> allTagPosts = tag.getPosts().stream()
                .filter(Post::isPublished)
                .collect(Collectors.toList());
        
        // 手动分页（简化实现）
        int start = filter.getPage() * filter.getSize();
        int end = Math.min(start + filter.getSize(), allTagPosts.size());
        
        if (start >= allTagPosts.size()) {
            return PageResponse.of(
                    List.of(),
                    filter.getPage(),
                    filter.getSize(),
                    allTagPosts.size(),
                    (int) Math.ceil((double) allTagPosts.size() / filter.getSize()),
                    true
            );
        }
        
        List<PostSummaryDto> content = allTagPosts.subList(start, end).stream()
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                content,
                filter.getPage(),
                filter.getSize(),
                allTagPosts.size(),
                (int) Math.ceil((double) allTagPosts.size() / filter.getSize()),
                end >= allTagPosts.size()
        );
    }

    @Override
    public PageResponse<PostSummaryDto> searchPosts(String keyword, PostFilter filter) {
        Pageable pageable = getPageable(filter);
        
        Page<Post> postsPage = postRepository.searchPosts(keyword, pageable);
        
        List<PostSummaryDto> content = postsPage.getContent().stream()
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
        
        return PageResponse.of(
                content,
                postsPage.getNumber(),
                postsPage.getSize(),
                postsPage.getTotalElements(),
                postsPage.getTotalPages(),
                postsPage.isLast()
        );
    }

    @Override
    public List<PostSummaryDto> getRecentPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by("createdAt").descending());
        return postRepository.findRecentPosts(pageable).stream()
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<PostSummaryDto> getPopularPosts(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return postRepository.findPopularPosts(pageable).stream()
                .map(this::mapToPostSummaryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PostDto createPost(PostCreateRequest postRequest, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));
        
        Category category = null;
        if (postRequest.getCategoryId() != null) {
            category = categoryRepository.findById(postRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postRequest.getCategoryId()));
        }
        
        Set<Tag> tags = new HashSet<>();
        if (postRequest.getTagIds() != null && !postRequest.getTagIds().isEmpty()) {
            tags = tagRepository.findByIdIn(postRequest.getTagIds());
        }
        
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .excerpt(postRequest.getExcerpt())
                .content(postRequest.getContent())
                .coverImage(postRequest.getCoverImage())
                .author(author)
                .category(category)
                .published(postRequest.isPublished())
                .build();
        
        // 添加标签
        for (Tag tag : tags) {
            post.addTag(tag);
        }
        
        Post savedPost = postRepository.save(post);
        return mapToPostDto(savedPost);
    }

    @Override
    @Transactional
    public PostDto updatePost(Long id, PostUpdateRequest postRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        // 只更新非null字段
        if (postRequest.getTitle() != null) {
            post.setTitle(postRequest.getTitle());
        }
        
        if (postRequest.getExcerpt() != null) {
            post.setExcerpt(postRequest.getExcerpt());
        }
        
        if (postRequest.getContent() != null) {
            post.setContent(postRequest.getContent());
        }
        
        if (postRequest.getCoverImage() != null) {
            post.setCoverImage(postRequest.getCoverImage());
        }
        
        if (postRequest.getPublished() != null) {
            post.setPublished(postRequest.getPublished());
        }
        
        // 更新分类
        if (postRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(postRequest.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", postRequest.getCategoryId()));
            post.setCategory(category);
        }
        
        // 更新标签
        if (postRequest.getTagIds() != null && !postRequest.getTagIds().isEmpty()) {
            // 移除所有现有标签
            for (Tag tag : new HashSet<>(post.getTags())) {
                post.removeTag(tag);
            }
            
            // 添加新标签
            Set<Tag> newTags = tagRepository.findByIdIn(postRequest.getTagIds());
            for (Tag tag : newTags) {
                post.addTag(tag);
            }
        }
        
        Post updatedPost = postRepository.save(post);
        return mapToPostDto(updatedPost);
    }

    @Override
    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        postRepository.delete(post);
    }

    @Override
    @Transactional
    public PostDto incrementViews(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        post.incrementViews();
        Post updatedPost = postRepository.save(post);
        
        return mapToPostDto(updatedPost);
    }

    @Override
    @Transactional
    public PostDto likePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        post.like();
        Post updatedPost = postRepository.save(post);
        
        return mapToPostDto(updatedPost);
    }

    @Override
    @Transactional
    public PostDto unlikePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        
        post.unlike();
        Post updatedPost = postRepository.save(post);
        
        return mapToPostDto(updatedPost);
    }
    
    // 辅助方法：将Post实体映射为PostDto
    private PostDto mapToPostDto(Post post) {
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .excerpt(post.getExcerpt())
                .content(post.getContent())
                .coverImage(post.getCoverImage())
                .author(mapToUserSummaryDto(post.getAuthor()))
                .category(post.getCategory() != null ? mapToCategoryDto(post.getCategory()) : null)
                .tags(post.getTags().stream().map(this::mapToTagDto).collect(Collectors.toSet()))
                .published(post.isPublished())
                .views(post.getViews())
                .likes(post.getLikes())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
    
    // 辅助方法：将Post实体映射为PostSummaryDto
    private PostSummaryDto mapToPostSummaryDto(Post post) {
        return PostSummaryDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .excerpt(post.getExcerpt())
                .coverImage(post.getCoverImage())
                .author(mapToUserSummaryDto(post.getAuthor()))
                .category(post.getCategory() != null ? mapToCategoryDto(post.getCategory()) : null)
                .tags(post.getTags().stream().map(this::mapToTagDto).collect(Collectors.toSet()))
                .commentCount(commentRepository.countByPost(post))
                .views(post.getViews())
                .likes(post.getLikes())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .published(post.isPublished())
                .build();
    }
    
    // 辅助方法：将User实体映射为UserSummaryDto
    private UserSummaryDto mapToUserSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
    
    // 辅助方法：将Category实体映射为CategoryDto
    private CategoryDto mapToCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }
    
    // 辅助方法：将Tag实体映射为TagDto
    private TagDto mapToTagDto(Tag tag) {
        return TagDto.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
    
    // 辅助方法：创建Pageable
    private Pageable getPageable(PostFilter filter) {
        Sort sort = Sort.by(
                "desc".equalsIgnoreCase(filter.getSortDirection()) 
                        ? Sort.Direction.DESC 
                        : Sort.Direction.ASC, 
                filter.getSortBy());
        
        return PageRequest.of(filter.getPage(), filter.getSize(), sort);
    }
} 