<template>
  <div class="blog">
    <!-- 左上角 Logo -->
    <AppLogo :fixed="true" routeTo="/home" />
    
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
import AppLogo from '../components/common/AppLogo.vue'
import { formatDate, mockPosts, getCategories } from '../utils/common.js'

const router = useRouter()

const posts = ref([])
const loading = ref(true)
const error = ref(null)
const searchQuery = ref('')
const selectedCategory = ref('')
const currentPage = ref(1)
const totalPages = ref(1)

const categories = getCategories()

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
  color: var(--text-primary);
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
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
  background-color: var(--background-lighter);
  color: var(--text-primary);
}

.category-filter select {
  padding: 0.5rem 1rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  font-size: 1rem;
  background-color: var(--background-lighter);
  color: var(--text-primary);
}

.post-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 1.5rem;
}

.post-card {
  background-color: var(--background-card);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s, box-shadow 0.3s;
  cursor: pointer;
  border: 1px solid var(--border-color);
}

.post-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.4);
  border-color: var(--primary-color);
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
  background-color: var(--primary-color);
  color: var(--background-dark);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.post-title {
  font-size: 1.25rem;
  margin-bottom: 0.5rem;
  line-height: 1.3;
  color: var(--text-primary);
}

.post-excerpt {
  color: var(--text-secondary);
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
  color: var(--text-secondary);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 2rem;
}

.page-btn {
  background-color: var(--primary-color);
  color: var(--background-dark);
  border: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
}

.page-btn:hover:not(:disabled) {
  background-color: var(--primary-variant);
  box-shadow: 0 0 10px rgba(255, 220, 0, 0.4);
}

.page-btn:disabled {
  background-color: var(--background-lighter);
  cursor: not-allowed;
  color: var(--text-disabled);
}

.page-info {
  margin: 0 1rem;
  color: var(--text-secondary);
}

.loading, .error, .no-posts {
  text-align: center;
  padding: 3rem 0;
  color: var(--text-secondary);
}

.error {
  color: #ef4444;
}
</style>