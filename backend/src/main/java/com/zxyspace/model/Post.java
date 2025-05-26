package com.zxyspace.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    private String title;

    @Size(max = 255)
    private String excerpt;

    @NotBlank
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private String coverImage;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany
    @JoinTable(
        name = "post_tags",
        joinColumns = @JoinColumn(name = "post_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    private boolean published = true;

    private int views = 0;

    private int likes = 0;
    
    // 辅助方法：添加标签
    public void addTag(Tag tag) {
        tags.add(tag);
        tag.getPosts().add(this);
    }
    
    // 辅助方法：移除标签
    public void removeTag(Tag tag) {
        tags.remove(tag);
        tag.getPosts().remove(this);
    }
    
    // 辅助方法：添加评论
    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
    }
    
    // 辅助方法：移除评论
    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
    }
    
    // 辅助方法：增加浏览量
    public void incrementViews() {
        this.views++;
    }
    
    // 辅助方法：点赞
    public void like() {
        this.likes++;
    }
    
    // 辅助方法：取消点赞
    public void unlike() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
}