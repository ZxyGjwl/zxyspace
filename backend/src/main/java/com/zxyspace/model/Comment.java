package com.zxyspace.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment extends BaseEntity {

    @NotBlank
    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int likes = 0;
    
    // 点赞方法
    public void like() {
        this.likes++;
    }
    
    // 取消点赞方法
    public void unlike() {
        if (this.likes > 0) {
            this.likes--;
        }
    }
    
    // 手动添加setPost方法，解决Lombok可能的编译问题
    public void setPost(Post post) {
        this.post = post;
    }
    
    public Post getPost() {
        return this.post;
    }
}