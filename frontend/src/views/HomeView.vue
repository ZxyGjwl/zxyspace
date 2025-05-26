<template>
  <div class="home" :class="{ 'fade-in': isVisible }">
    <section class="hero-section">
      <h1>欢迎来到个人空间</h1>
      <p class="subtitle">探索技术、生活与创意的交汇点</p>
    </section>
    
    <div class="feature-grid">
      <div class="feature-card" :class="{ 'slide-in': isVisible }" style="--delay: 0.1s">
        <h2>最新博客</h2>
        <div v-if="loading" class="loading">
          <div class="loading-spinner"></div>
          <span>加载中...</span>
        </div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <ul v-else class="post-list">
          <li v-for="post in recentPosts" :key="post.id" class="post-item">
            <RouterLink :to="`/blog/${post.id}`">
              <div class="post-content">
                <div class="post-meta">
                  <span class="post-category">{{ post.category }}</span>
                  <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                </div>
                <h3>{{ post.title }}</h3>
                <p class="post-excerpt">{{ post.excerpt }}</p>
              </div>
            </RouterLink>
          </li>
        </ul>
        <div class="card-footer">
          <RouterLink to="/blog" class="btn btn-text">查看全部博客 <span class="arrow">→</span></RouterLink>
        </div>
      </div>
      
      <div class="feature-card about-card" :class="{ 'slide-in': isVisible }" style="--delay: 0.2s">
        <h2>关于我</h2>
        <div class="about-content">
          <p>欢迎访问我的个人空间！这里记录我的技术心得与生活点滴。</p>
          <p>作为一名热爱技术的开发者，我致力于分享有价值的内容和经验。</p>
        </div>
        <div class="card-footer">
          <RouterLink to="/about" class="btn btn-text">了解更多 <span class="arrow">→</span></RouterLink>
        </div>
      </div>
      
      <div class="feature-card" :class="{ 'slide-in': isVisible }" style="--delay: 0.3s">
        <h2>技术栈</h2>
        <div class="tech-tags">
          <span class="tag">Vue 3</span>
          <span class="tag">JavaScript</span>
          <span class="tag">Node.js</span>
          <span class="tag">Spring Boot</span>
          <span class="tag">Docker</span>
          <span class="tag">AWS</span>
        </div>
        <div class="card-footer">
          <RouterLink to="/about" class="btn btn-text">查看详情 <span class="arrow">→</span></RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated, defineExpose, nextTick } from 'vue'
import { RouterLink } from 'vue-router'
import axios from 'axios'

const recentPosts = ref([])
const loading = ref(true)
const error = ref(null)
let dataLoaded = false
const isVisible = ref(false)

// 预加载状态
const preLoaded = ref(false)

// 模拟数据 - 实际应用中应从后端API获取
const mockPosts = [
  {
    id: 1,
    title: '如何使用Vue3构建现代化前端应用',
    excerpt: 'Vue3带来了许多新特性，包括Composition API、Teleport、Fragments等...',
    createdAt: '2023-04-15T10:30:00',
    category: '前端开发'
  },
  {
    id: 2,
    title: 'Spring Boot最佳实践',
    excerpt: '本文将介绍Spring Boot开发中的一些常见问题和解决方案...',
    createdAt: '2023-04-10T14:20:00',
    category: '后端开发'
  },
  {
    id: 3,
    title: '我的旅行日记：杭州西湖',
    excerpt: '上周末去了杭州，游览了风景如画的西湖，体验了当地的文化和美食...',
    createdAt: '2023-04-05T08:15:00',
    category: '生活随笔'
  }
]

// 预加载数据方法
const preloadData = () => {
  if (dataLoaded) return Promise.resolve(recentPosts.value)
  
  return new Promise((resolve) => {
    // 使用更快的加载时间，因为这是预加载
    setTimeout(() => {
      recentPosts.value = mockPosts
      loading.value = false
      dataLoaded = true
      preLoaded.value = true
      
      // 预渲染组件并立即显示
      nextTick(() => {
        // 立即显示内容，不等待过渡
        isVisible.value = true
        // 为了确保DOM已更新
        setTimeout(() => {
          resolve(recentPosts.value)
        }, 50)
      })
    }, 50) // 更短的加载时间用于预加载
  })
}

// 设置组件可见性的方法
const setVisible = (visible) => {
  isVisible.value = visible;
}

const loadData = () => {
  if (dataLoaded) return

  try {
    // 实际应用中应从API获取数据
    // const response = await axios.get('/api/posts/recent')
    // recentPosts.value = response.data
    
    // 模拟API请求，如果已经预加载过则不再加载
    if (!preLoaded.value) {
      setTimeout(() => {
        recentPosts.value = mockPosts
        loading.value = false
        dataLoaded = true
        
        // 数据加载后立即显示，不添加延迟
        isVisible.value = true
      }, 50) // 减少加载时间
    } else {
      // 如果已预加载，立即显示
      isVisible.value = true
    }
  } catch (err) {
    error.value = '获取数据失败，请稍后再试'
    loading.value = false
    console.error(err)
  }
}

onMounted(() => {
  loadData()
  
  // 如果是直接访问而不是通过过渡，立即显示
  if (document.referrer === '' || !document.referrer.includes('splash')) {
    isVisible.value = true
  }
})

// 组件被激活时（从缓存中恢复）
onActivated(() => {
  if (!dataLoaded) {
    loadData()
  } else {
    // 如果数据已加载，直接显示
    isVisible.value = true
  }
})

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 暴露方法给父组件
defineExpose({
  preloadData,
  setVisible
})
</script>

<style scoped lang="scss">
.home {
  width: 100%;
  will-change: transform, opacity;
  transform: translateZ(0);
  backface-visibility: hidden;
  opacity: 1;
  transition: opacity 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  max-width: 100%;
  box-sizing: border-box;
}

.hero-section {
  text-align: center;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg) 0;
  
  h1 {
    margin-bottom: var(--spacing-md);
    font-size: var(--font-size-xxl);
    font-weight: var(--font-weight-bold);
    color: var(--text-primary);
    letter-spacing: -0.02em;
  }
  
  .subtitle {
    color: var(--text-secondary);
    font-size: var(--font-size-lg);
    max-width: 600px;
    margin: 0 auto;
  }
}

.feature-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: var(--spacing-lg);
  width: 100%;
  opacity: 1;
  transition: opacity 0.3s ease-in;
}

.feature-card {
  background-color: var(--background-card);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  box-shadow: var(--box-shadow);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transform: translateZ(0) translateY(0);
  opacity: 1;
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1) var(--delay, 0s), 
              opacity 0.3s cubic-bezier(0.22, 1, 0.36, 1) var(--delay, 0s),
              box-shadow 0.3s ease;
  
  &:hover {
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.2);
  }
  
  h2 {
    color: var(--primary-color);
    margin-bottom: var(--spacing-md);
    font-size: var(--font-size-xl);
    font-weight: var(--font-weight-bold);
    letter-spacing: -0.01em;
  }
  
  &.about-card {
    background: linear-gradient(145deg, var(--background-card), var(--background-lighter));
  }
}

.post-list {
  list-style: none;
  padding: 0;
  margin: var(--spacing-md) 0;
}

.post-item {
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  transition: transform 0.2s ease;
  
  &:last-child {
    margin-bottom: 0;
    padding-bottom: 0;
    border-bottom: none;
  }
  
  &:hover {
    transform: translateY(-2px);
    
    h3 {
      color: var(--primary-color);
    }
  }
  
  a {
    text-decoration: none;
  }
}

.post-content {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.post-meta {
  display: flex;
  gap: var(--spacing-md);
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
}

.post-category {
  color: var(--primary-color);
  font-weight: var(--font-weight-medium);
}

.post-item h3 {
  color: var(--text-primary);
  margin: var(--spacing-xs) 0;
  transition: color 0.2s ease;
  font-size: var(--font-size-lg);
}

.post-excerpt {
  color: var(--text-secondary);
  line-height: 1.6;
  font-size: var(--font-size-md);
}

.card-footer {
  margin-top: var(--spacing-lg);
  text-align: right;
  
  .btn-text {
    display: inline-flex;
    align-items: center;
    gap: var(--spacing-xs);
    font-weight: var(--font-weight-medium);
    
    .arrow {
      transition: transform 0.2s ease;
    }
    
    &:hover .arrow {
      transform: translateX(4px);
    }
  }
}

.about-content {
  color: var(--text-secondary);
  line-height: 1.6;
  
  p {
    margin-bottom: var(--spacing-md);
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin: var(--spacing-md) 0;
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-lg) 0;
  color: var(--text-secondary);
  gap: var(--spacing-sm);
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.1);
  border-top: 2px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  color: var(--danger-color);
  padding: var(--spacing-md);
  text-align: center;
  background-color: rgba(255, 69, 58, 0.1);
  border-radius: var(--border-radius-md);
}

@media (min-width: 768px) {
  .feature-grid {
    grid-template-columns: 2fr 1fr;
    grid-template-rows: auto auto;
  }
  
  .feature-card:first-child {
    grid-row: span 2;
  }
}

@media (min-width: 1024px) {
  .feature-grid {
    grid-template-columns: 2fr 1fr 1fr;
    grid-template-rows: auto;
  }
  
  .feature-card:first-child {
    grid-row: auto;
  }
}
</style>