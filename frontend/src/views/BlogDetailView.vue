<template>
  <div class="blog-detail">
    <!-- 左上角 Logo -->
    <AppLogo :fixed="true" routeTo="/home" />
    
    <div v-if="loading" class="loading">
      加载中...
    </div>
    
    <div v-else-if="error" class="error">
      {{ error }}
      <div class="error-action">
        <RouterLink to="/blog">返回博客列表</RouterLink>
      </div>
    </div>
    
    <div v-else-if="post" class="post-container">
      <div class="post-header">
        <div class="post-meta">
          <span class="post-category">{{ post.category }}</span>
          <span class="post-date">{{ formatDate(post.createdAt) }}</span>
        </div>
        <h1 class="post-title">{{ post.title }}</h1>
        <div class="post-author">
          <img :src="post.authorAvatar || '/user-avatar.png'" alt="作者头像" class="author-avatar">
          <span>{{ post.author }}</span>
        </div>
      </div>
      
      <div class="post-cover" v-if="post.coverImage">
        <img :src="post.coverImage" alt="文章封面">
      </div>
      
      <div class="post-content" v-html="post.content"></div>
      
      <div class="post-tags" v-if="post.tags && post.tags.length">
        <span class="tag-label">标签:</span>
        <div class="tags-container">
          <span 
            v-for="tag in post.tags" 
            :key="tag" 
            class="tag"
          >
            {{ tag }}
          </span>
        </div>
      </div>
      
      <div class="post-actions">
        <button class="action-btn like-btn" @click="toggleLike">
          <span class="icon">❤️</span>
          <span>{{ post.liked ? '已赞' : '点赞' }} ({{ post.likes }})</span>
        </button>
        
        <button class="action-btn share-btn" @click="sharePost">
          <span class="icon">🔗</span>
          <span>分享</span>
        </button>
      </div>
      
      <div class="post-navigation">
        <div class="prev-post" v-if="prevPost">
          <RouterLink :to="`/blog/${prevPost.id}`">
            <span class="nav-label">上一篇</span>
            <span class="nav-title">{{ prevPost.title }}</span>
          </RouterLink>
        </div>
        
        <div class="next-post" v-if="nextPost">
          <RouterLink :to="`/blog/${nextPost.id}`">
            <span class="nav-label">下一篇</span>
            <span class="nav-title">{{ nextPost.title }}</span>
          </RouterLink>
        </div>
      </div>
      
      <div class="comments-section">
        <h2>评论 ({{ post.comments.length }})</h2>
        
        <div class="comment-form">
          <textarea 
            v-model="newComment" 
            placeholder="写下你的评论..." 
            rows="4"
          ></textarea>
          <button @click="addComment" :disabled="!newComment.trim()">发表评论</button>
        </div>
        
        <div class="comments-list" v-if="post.comments.length">
          <div 
            v-for="(comment, index) in post.comments" 
            :key="index" 
            class="comment"
          >
            <div class="comment-author">
              <img :src="comment.avatar || '/user-avatar.png'" alt="评论者头像" class="comment-avatar">
              <div>
                <div class="comment-name">{{ comment.name }}</div>
                <div class="comment-date">{{ formatDate(comment.date) }}</div>
              </div>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <button class="comment-like" @click="likeComment(index)">
                👍 {{ comment.likes }}
              </button>
              <button class="comment-reply" @click="replyToComment(index)">
                回复
              </button>
            </div>
          </div>
        </div>
        
        <div v-else class="no-comments">
          暂无评论，快来发表第一条评论吧！
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter, RouterLink } from 'vue-router'
import AppLogo from '../components/common/AppLogo.vue'
import { formatDate, getPostById, mockPosts } from '../utils/common.js'

const route = useRoute()
const router = useRouter()
const postId = computed(() => Number(route.params.id))

const post = ref(null)
const loading = ref(true)
const error = ref(null)
const newComment = ref('')



onMounted(() => {
  fetchPost()
})

const fetchPost = () => {
  loading.value = true
  error.value = null
  
  // 模拟API请求
  setTimeout(() => {
    try {
      const foundPost = getPostById(postId.value)
      
      if (foundPost) {
        post.value = foundPost
      } else {
        error.value = '文章不存在或已被删除'
      }
      
      loading.value = false
    } catch (err) {
      error.value = '获取数据失败，请稍后再试'
      loading.value = false
      console.error(err)
    }
  }, 500)
}

const prevPost = computed(() => {
  if (!post.value) return null
  const currentIndex = mockPosts.findIndex(p => p.id === post.value.id)
  return currentIndex > 0 ? mockPosts[currentIndex - 1] : null
})

const nextPost = computed(() => {
  if (!post.value) return null
  const currentIndex = mockPosts.findIndex(p => p.id === post.value.id)
  return currentIndex < mockPosts.length - 1 ? mockPosts[currentIndex + 1] : null
})

const toggleLike = () => {
  if (post.value) {
    if (post.value.liked) {
      post.value.likes--
    } else {
      post.value.likes++
    }
    post.value.liked = !post.value.liked
  }
}

const sharePost = () => {
  // 实际应用中应实现分享功能
  alert('分享链接已复制到剪贴板')
}

const addComment = () => {
  if (!post.value || !newComment.value.trim()) return
  
  post.value.comments.unshift({
    name: '当前用户', // 实际应用中应获取当前登录用户信息
    avatar: 'https://randomuser.me/api/portraits/men/9.jpg',
    date: new Date().toISOString(),
    content: newComment.value.trim(),
    likes: 0
  })
  
  newComment.value = ''
}

const likeComment = (index) => {
  if (post.value && post.value.comments[index]) {
    post.value.comments[index].likes++
  }
}

const replyToComment = (index) => {
  if (post.value && post.value.comments[index]) {
    const commentAuthor = post.value.comments[index].name
    newComment.value = `@${commentAuthor} `
    // 滚动到评论表单
    document.querySelector('.comment-form textarea').focus()
  }
}


</script>

<style scoped>
.blog-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 2rem 1rem;
}

.post-container {
  background-color: var(--background-card);
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color);
}

.post-header {
  padding: 2rem;
  text-align: center;
}

.post-title {
  margin-bottom: 1.5rem;
  font-size: 2rem;
  line-height: 1.2;
  color: var(--text-primary);
}

.post-meta {
  display: flex;
  justify-content: center;
  gap: 1rem;
  margin-bottom: 1rem;
  font-size: 0.875rem;
  color: var(--text-secondary);
}

.post-category {
  display: inline-block;
  background-color: var(--primary-color);
  color: var(--background-dark);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-weight: 600;
}

.post-author {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  color: var(--text-primary);
}

.author-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--primary-color);
}

.post-cover img {
  width: 100%;
  max-height: 400px;
  object-fit: cover;
}

.post-content {
  padding: 2rem;
  line-height: 1.7;
  color: var(--text-primary);
}

.post-content h2 {
  margin-top: 2rem;
  margin-bottom: 1rem;
  color: var(--primary-color);
}

.post-content p {
  margin-bottom: 1.5rem;
}

.post-content ul, .post-content ol {
  margin-bottom: 1.5rem;
  padding-left: 1.5rem;
}

.post-content li {
  margin-bottom: 0.5rem;
}

.post-content pre {
  background-color: var(--background-lighter);
  padding: 1rem;
  border-radius: 4px;
  overflow-x: auto;
  margin-bottom: 1.5rem;
}

.post-content code {
  font-family: 'JetBrains Mono', monospace;
  color: var(--primary-color);
}

.post-tags {
  display: flex;
  align-items: center;
  padding: 0 2rem 1rem;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag-label {
  color: var(--text-secondary);
  font-size: 0.875rem;
}

.tags-container {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  background-color: var(--background-lighter);
  color: var(--primary-color);
  padding: 0.25rem 0.5rem;
  border-radius: 4px;
  font-size: 0.75rem;
  border: 1px solid var(--primary-color);
}

.post-actions {
  display: flex;
  justify-content: space-around;
  padding: 1rem 2rem;
  border-top: 1px solid var(--border-color);
  border-bottom: 1px solid var(--border-color);
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: none;
  border: none;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: all 0.3s;
  color: var(--text-primary);
}

.action-btn:hover {
  background-color: var(--background-lighter);
  color: var(--primary-color);
}

.like-btn {
  color: var(--text-primary);
}

.post-navigation {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  padding: 1.5rem 2rem;
  border-bottom: 1px solid var(--border-color);
}

.prev-post, .next-post {
  display: flex;
}

.prev-post {
  justify-content: flex-start;
}

.next-post {
  justify-content: flex-end;
  text-align: right;
}

.nav-label {
  display: block;
  font-size: 0.75rem;
  color: var(--text-secondary);
  margin-bottom: 0.25rem;
}

.nav-title {
  color: var(--primary-color);
  font-weight: 600;
}

.comments-section {
  padding: 2rem;
}

.comments-section h2 {
  margin-bottom: 1.5rem;
  color: var(--text-primary);
}

.comment-form {
  margin-bottom: 2rem;
}

.comment-form textarea {
  width: 100%;
  padding: 1rem;
  border: 1px solid var(--border-color);
  border-radius: 4px;
  resize: vertical;
  margin-bottom: 1rem;
  font-family: inherit;
  font-size: 1rem;
  background-color: var(--background-lighter);
  color: var(--text-primary);
}

.comment-form button {
  background-color: var(--primary-color);
  color: var(--background-dark);
  border: none;
  padding: 0.5rem 1.5rem;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  font-weight: 600;
}

.comment-form button:hover:not(:disabled) {
  background-color: var(--primary-variant);
  box-shadow: 0 0 10px rgba(255, 220, 0, 0.4);
}

.comment-form button:disabled {
  background-color: var(--background-lighter);
  cursor: not-allowed;
  color: var(--text-disabled);
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.comment {
  background-color: var(--background-lighter);
  padding: 1.5rem;
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.comment-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid var(--primary-color);
}

.comment-name {
  font-weight: 600;
  color: var(--text-primary);
}

.comment-date {
  font-size: 0.75rem;
  color: var(--text-secondary);
}

.comment-content {
  margin-bottom: 1rem;
  color: var(--text-primary);
}

.comment-actions {
  display: flex;
  gap: 1rem;
}

.comment-like, .comment-reply {
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-secondary);
  padding: 0;
  font-size: 0.875rem;
  transition: color 0.3s;
}

.comment-like:hover, .comment-reply:hover {
  color: var(--primary-color);
}

.no-comments {
  text-align: center;
  padding: 1.5rem;
  color: var(--text-secondary);
  background-color: var(--background-lighter);
  border-radius: 8px;
  border: 1px solid var(--border-color);
}

.loading, .error {
  text-align: center;
  padding: 3rem 0;
  color: var(--text-secondary);
}

.error {
  color: #ef4444;
}

.error-action {
  margin-top: 1rem;
}

.error-action a {
  color: var(--primary-color);
  text-decoration: underline;
}
</style>