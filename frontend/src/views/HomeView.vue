<template>
  <div class="home">
    <h1>欢迎来到个人空间</h1>
    <div class="feature-section">
      <div class="feature">
        <h2>最新博客</h2>
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        <ul v-else class="post-list">
          <li v-for="post in recentPosts" :key="post.id" class="post-item">
            <RouterLink :to="`/blog/${post.id}`">
              <h3>{{ post.title }}</h3>
              <p class="post-excerpt">{{ post.excerpt }}</p>
              <div class="post-meta">
                <span class="post-date">{{ formatDate(post.createdAt) }}</span>
                <span class="post-category">{{ post.category }}</span>
              </div>
            </RouterLink>
          </li>
        </ul>
        <div class="more-link">
          <RouterLink to="/blog">查看全部博客 →</RouterLink>
        </div>
      </div>
      
      <div class="feature">
        <h2>关于我</h2>
        <p>欢迎访问我的个人空间！这里记录我的技术心得与生活点滴。</p>
        <div class="more-link">
          <RouterLink to="/about">了解更多 →</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { RouterLink } from 'vue-router'
import axios from 'axios'

const recentPosts = ref([])
const loading = ref(true)
const error = ref(null)

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

onMounted(async () => {
  try {
    // 实际应用中应从API获取数据
    // const response = await axios.get('/api/posts/recent')
    // recentPosts.value = response.data
    
    // 模拟API请求
    setTimeout(() => {
      recentPosts.value = mockPosts
      loading.value = false
    }, 500)
  } catch (err) {
    error.value = '获取数据失败，请稍后再试'
    loading.value = false
    console.error(err)
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
</script>

<style scoped>
.home {
  width: 100%;
}

h1 {
  margin-bottom: 2rem;
  text-align: center;
}

.feature-section {
  display: grid;
  grid-template-columns: 1fr;
  gap: 2rem;
}

.feature {
  background-color: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.post-list {
  list-style: none;
  padding: 0;
  margin: 1rem 0;
}

.post-item {
  margin-bottom: 1.5rem;
  padding-bottom: 1.5rem;
  border-bottom: 1px solid var(--color-border);
}

.post-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
  padding-bottom: 0;
}

.post-item a {
  color: inherit;
  text-decoration: none;
  display: block;
}

.post-item h3 {
  margin-bottom: 0.5rem;
  transition: color 0.3s;
}

.post-item a:hover h3 {
  color: var(--color-primary);
}

.post-excerpt {
  color: var(--color-text-light);
  margin-bottom: 0.5rem;
}

.post-meta {
  display: flex;
  font-size: 0.875rem;
  color: var(--color-text-light);
}

.post-date {
  margin-right: 1rem;
}

.more-link {
  margin-top: 1rem;
  text-align: right;
}

.loading, .error {
  text-align: center;
  padding: 2rem 0;
  color: var(--color-text-light);
}

.error {
  color: #ef4444;
}

@media (min-width: 768px) {
  .feature-section {
    grid-template-columns: 2fr 1fr;
  }
}
</style>