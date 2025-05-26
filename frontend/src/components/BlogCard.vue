<template>
  <div class="blog-card" @mousemove="handleMouseMove" @mouseleave="handleMouseLeave" :style="cardStyle">
    <div class="card-highlight"></div>
    <div class="card-content">
      <div class="card-image" v-if="post.coverImage">
        <img :src="post.coverImage" :alt="post.title" />
        <div class="image-overlay"></div>
      </div>
      
      <div class="card-category" v-if="post.category">
        <span class="tech-badge">{{ post.category.name }}</span>
      </div>
      
      <h3 class="card-title animated-underline">{{ post.title }}</h3>
      
      <p class="card-excerpt">{{ post.excerpt }}</p>
      
      <div class="card-meta">
        <div class="meta-item">
          <i class="el-icon-view"></i>
          <span>{{ post.views || 0 }}</span>
        </div>
        <div class="meta-item">
          <i class="el-icon-star-on"></i>
          <span>{{ post.likes || 0 }}</span>
        </div>
        <div class="meta-item">
          <i class="el-icon-date"></i>
          <span>{{ formatDate(post.createdAt) }}</span>
        </div>
      </div>
      
      <div class="card-tags" v-if="post.tags && post.tags.length">
        <span class="tag" v-for="tag in post.tags" :key="tag.id">{{ tag.name }}</span>
      </div>
      
      <div class="card-author" v-if="post.author">
        <img v-if="post.author.avatar" :src="post.author.avatar" :alt="post.author.username" />
        <span>{{ post.author.username }}</span>
      </div>
      
      <div class="card-footer">
        <router-link :to="{ name: 'blog-detail', params: { id: post.id } }" class="read-more-btn">
          阅读更多
          <div class="btn-highlight"></div>
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';

const props = defineProps({
  post: {
    type: Object,
    required: true
  }
});

// 鼠标效果状态
const mouseX = ref(0);
const mouseY = ref(0);
const isHovering = ref(false);

// 计算卡片样式（实现倾斜效果）
const cardStyle = computed(() => {
  if (!isHovering.value) return {};
  
  const cardWidth = 300; // 假设的卡片宽度
  const cardHeight = 400; // 假设的卡片高度
  
  // 计算鼠标相对卡片中心的位置（-0.5到0.5范围）
  const centerX = (mouseX.value / cardWidth) - 0.5;
  const centerY = (mouseY.value / cardHeight) - 0.5;
  
  // 转换为倾斜角度（最大倾斜5度）
  const tiltX = centerY * 5;
  const tiltY = -centerX * 5;
  
  return {
    transform: `perspective(1000px) rotateX(${tiltX}deg) rotateY(${tiltY}deg) scale3d(1.03, 1.03, 1.03)`,
    boxShadow: `
      0 5px 15px rgba(0, 0, 0, 0.3),
      0 0 5px rgba(255, 220, 0, 0.3),
      ${centerX * 20}px ${centerY * 20}px 20px rgba(255, 220, 0, 0.1)
    `
  };
});

// 鼠标移动处理
const handleMouseMove = (e) => {
  const rect = e.currentTarget.getBoundingClientRect();
  mouseX.value = e.clientX - rect.left;
  mouseY.value = e.clientY - rect.top;
  isHovering.value = true;
};

// 鼠标离开处理
const handleMouseLeave = () => {
  isHovering.value = false;
};

// 日期格式化
const formatDate = (dateString) => {
  if (!dateString) return '';
  
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};
</script>

<style lang="scss" scoped>
.blog-card {
  position: relative;
  background-color: var(--background-card);
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  height: 100%;
  display: flex;
  flex-direction: column;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  border: 1px solid var(--border-color);
  will-change: transform;
  
  &:hover {
    .card-highlight {
      opacity: 1;
    }
    
    .read-more-btn {
      .btn-highlight {
        width: 100%;
      }
    }
    
    .card-image img {
      transform: scale(1.05);
    }
    
    .image-overlay {
      opacity: 0.4;
    }
  }
}

.card-highlight {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, 
    rgba(255, 220, 0, 0) 0%, 
    rgba(255, 220, 0, 0.1) 50%, 
    rgba(255, 220, 0, 0) 100%
  );
  pointer-events: none;
  opacity: 0;
  transition: opacity 0.3s ease;
  z-index: 1;
}

.card-content {
  display: flex;
  flex-direction: column;
  height: 100%;
  padding: 0;
  position: relative;
  z-index: 2;
}

.card-image {
  position: relative;
  height: 180px;
  overflow: hidden;
  
  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.5s ease;
  }
  
  .image-overlay {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(to bottom, 
      rgba(18, 18, 18, 0.2) 0%, 
      rgba(18, 18, 18, 0.8) 100%
    );
    opacity: 0.6;
    transition: opacity 0.3s ease;
  }
}

.card-category {
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 3;
  
  .tech-badge {
    background-color: var(--primary-color);
    color: var(--background-dark);
    font-size: 0.8rem;
    padding: 4px 8px;
    border-radius: 4px;
    font-weight: 600;
    box-shadow: 0 0 10px rgba(255, 220, 0, 0.4);
  }
}

.card-title {
  margin: 0;
  padding: 16px 16px 8px;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--primary-color);
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  transition: color 0.3s ease;
}

.card-excerpt {
  padding: 0 16px 16px;
  margin: 0;
  color: var(--text-secondary);
  font-size: 0.9rem;
  line-height: 1.6;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  flex-grow: 1;
}

.card-meta {
  display: flex;
  padding: 0 16px 16px;
  color: var(--text-muted);
  font-size: 0.8rem;
  
  .meta-item {
    display: flex;
    align-items: center;
    margin-right: 12px;
    
    i {
      margin-right: 4px;
      font-size: 0.9rem;
    }
  }
}

.card-tags {
  padding: 0 16px 16px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
  .tag {
    font-size: 0.7rem;
    padding: 2px 8px;
    border-radius: 12px;
    background-color: var(--background-lighter);
    color: var(--text-secondary);
    border: 1px solid var(--border-color);
    transition: all 0.3s ease;
    
    &:hover {
      background-color: var(--primary-color);
      color: var(--background-dark);
    }
  }
}

.card-author {
  padding: 16px;
  display: flex;
  align-items: center;
  border-top: 1px solid var(--border-color);
  
  img {
    width: 30px;
    height: 30px;
    border-radius: 50%;
    margin-right: 8px;
    object-fit: cover;
    border: 1px solid var(--primary-color);
  }
  
  span {
    color: var(--text-secondary);
    font-size: 0.9rem;
  }
}

.card-footer {
  padding: 16px;
  border-top: 1px solid var(--border-color);
}

.read-more-btn {
  display: inline-block;
  position: relative;
  background-color: var(--background-lighter);
  color: var(--primary-color);
  padding: 8px 16px;
  border-radius: 4px;
  font-weight: 500;
  font-size: 0.9rem;
  overflow: hidden;
  text-decoration: none;
  z-index: 1;
  
  .btn-highlight {
    position: absolute;
    top: 0;
    left: 0;
    width: 0;
    height: 100%;
    background-color: var(--primary-color);
    transition: width 0.3s ease;
    z-index: -1;
  }
  
  &:hover {
    color: var(--background-dark);
  }
}

// 响应式调整
@media (max-width: 768px) {
  .card-image {
    height: 140px;
  }
  
  .card-title {
    font-size: 1.1rem;
  }
}
</style>