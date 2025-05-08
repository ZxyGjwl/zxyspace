<template>
  <div class="blog">
    <h1>博客文章</h1>
    
    <div class="filters">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="搜索文章..." 
          @input="fetchPosts"
        />
      </div>
      
      <div class="category-filter">
        <select v-model="selectedCategory" @change="fetchPosts">
          <option value="">全部分类</option>
          <option v-for="category in categories" :key="category" :value="category">
            {{ category }}
          </option>
        </select>
      </div>
    </div>
    
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <div v-else-if="error" class="error">
      {{ error }}
    </div>
    
    <div v-else>
      <div v-if="posts.length === 0" class="no-posts">
        暂无文章
      </div>
      
      <div v-else class="post-grid">
        <div 
          v-for="post in posts" 
          :key="post.id" 
          class="post-card"
          @click="goToPost(post.id)"
        >
          <div class="post-image" :style="{ backgroundImage: `url(${post.coverImage || '/placeholder.jpg'})` }"></div>
          <div class="post-content">
            <div class="post-category">{{ post.category }}</div>
            <h2 class="post-title">{{ post.title }}</h2>
            <p class="post-excerpt">{{ post.excerpt }}</p>
            <div class="post-meta">
              <span class="post-date">{{ formatDate(post.createdAt) }}</span>
              <span class="post-author">{{ post.author }}</span>
            </div>
          </div>
        </div>
      </div>
      
      <div class="pagination">
        <button 
          :disabled="currentPage === 1" 
          @click="changePage(currentPage - 1)"
          class="page-btn"
        >
          上一页
        </button>
        
        <span class="page-info">{{ currentPage }} / {{ totalPages }}</span>
        
        <button 
          :disabled="currentPage === totalPages" 
          @click="changePage(currentPage + 1)"
          class="page-btn"
        >
          下一页
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const posts = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const totalPages = ref(1)

// 模拟数据 - 实际应用中应从后端API获取
const mockPosts = [
  {
    id: 1,
    title: '如何使用Vue3构建现代化前端应用',
    excerpt: 'Vue3带来了许多新特性，包括Composition API、Teleport、Fragments等...',
    createdAt: '2023-04-15T10:30:00',
    category: '前端开发',
    author: '张三',
    coverImage: 'https://picsum.photos/id/0/600/400'
  },
  {
    id: 2,
    title: 'Spring Boot最佳实践',
    excerpt: '本文将介绍Spring Boot开发中的一些常见问题和解决方案...',
    createdAt: '2023-04-10T14:20:00',
    category: '后端开发',
    author: '李四',
    coverImage: 'https://picsum.photos/id/1/600/400'
  },
  {
    id: 3,
    title: '我的旅行日记：杭州西湖',
    excerpt: '上周末去了杭州，游览了风景如画的西湖，体验了当地的文化和美食...',
    createdAt: '2023-04-05T08:15:00',
    category: '生活随笔',
    author: '王五',
    coverImage: 'https://picsum.photos/id/26/600/400'
  },
  {
    id: 4,
    title: 'Docker入门指南',
    excerpt: 'Docker可以让开发者打包他们的应用以及依赖包到一个轻量级、可移植的容器中...',
    createdAt: '2023-03-28T16:45:00',
    category: '运维',
    author: '赵六',
    coverImage: 'https://picsum.photos/id/60/600/400'
  },
  {
    id: 5,
    title: 'JavaScript异步编程详解',
    excerpt: '从回调函数到Promise，再到async/await，JavaScript的异步编程模式经历了怎样的演变...',
    createdAt: '2023-03-20T09:00:00',
    category: '前端开发',
    author: '张三',
    coverImage: 'https://picsum.photos/id/3/600/400'
  },
  {
    id: 6,
    title: '数据结构与算法：二叉树遍历',
    excerpt: '二叉树是计算机科学中常用的数据结构，了解如何遍历二叉树对于解决许多问题至关重要...',
    createdAt: '2023-03-15T11:30:00',
    category: '算法',
    author: '李四',
    coverImage: 'https://picsum.photos/id/94/600/400'
  }
]

const categories = ['前端开发', '后端开发', '算法', '运维', '生活随笔']

onMounted(() => {
  fetchPosts()
})

const fetchPosts = () => {
  loading.value = true
  error.value = null
  
  // 模拟API请求
  setTimeout(() => {
    try {
      // 过滤文章
      let filteredPosts = [...mockPosts]
      
      if (searchQuery.value) {
        const query = searchQuery.value.toLowerCase()
        filteredPosts = filteredPosts.filter(post => 
          post.title.toLowerCase().includes(query) || 
          post.excerpt.toLowerCase().includes(query)
        )
      }
      
      if (selectedCategory.value) {
        filteredPosts = filteredPosts.filter(post => 
          post.category === selectedCategory.value
        )
      }
      
      // 分页
      totalPages.value = Math.ceil(filteredPosts.length / 4) || 1
      if (currentPage.value > totalPages.value) {
        currentPage.value = totalPages.value
      }
      
      const startIndex = (currentPage.value - 1) * 4
      posts.value = filteredPosts.slice(startIndex, startIndex + 4)
      
      loading.value = false
    } catch (err) {
      error.value = '获取数据失败，请稍后再试'
      loading.value = false
      console.error(err)
    }
  }, 500)
}

const changePage = (page) => {
  currentPage.value = page
  fetchPosts()
}

const goToPost = (id) => {
  router.push(`/blog/${id}`)
}

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
.blog {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

h1 {
  text-align: center;
  margin-bottom: 2rem;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
  margin-bottom: 2rem;
}

.search-box {
  flex: 1;
  min-width: 200px;
}

.search-box input {
  width: 100%;
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 1rem;
}

.category-filter select {
  padding: 0.5rem 1rem;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  font-size: 1rem;
  background-color: white;
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.post-card {
  background-color: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
}

.post-image {
  height: 180px;
  background-size: cover;
  background-position: center;
}

.post-content {
  padding: 1.5rem;
}

.post-category {
  display: inline-block;
  background-color: var(--color-background-soft);
  color: var(--color-text-light);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  margin-bottom: 0.5rem;
}

.post-title {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
  line-height: 1.3;
}

.post-excerpt {
  color: var(--color-text-light);
  margin-bottom: 1rem;
  font-size: 0.875rem;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.post-meta {
  display: flex;
  justify-content: space-between;
  font-size: 0.75rem;
  color: var(--color-text-light);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
}

.page-btn {
  background-color: var(--color-primary);
  color: white;
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.page-btn:hover:not(:disabled) {
  background-color: var(--color-primary-hover);
}

.page-btn:disabled {
  background-color: var(--color-background-mute);
  cursor: not-allowed;
}

.page-info {
  margin: 0 1rem;
}

.loading, .error, .no-posts {
  text-align: center;
  padding: 3rem 0;
  color: var(--color-text-light);
}

.error {
  color: #ef4444;
}
</style>