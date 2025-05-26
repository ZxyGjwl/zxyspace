<template>
  <div class="blog-detail">
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

const route = useRoute()
const router = useRouter()
const postId = computed(() => Number(route.params.id))

const post = ref(null)
const loading = ref(true)
const error = ref(null)
const newComment = ref('')

// 模拟数据 - 实际应用中应从后端API获取
const mockPosts = [
  {
    id: 1,
    title: '如何使用Vue3构建现代化前端应用',
    excerpt: 'Vue3带来了许多新特性，包括Composition API、Teleport、Fragments等...',
    content: `
      <p>Vue 3是Vue.js的最新主要版本，引入了许多令人兴奋的新功能和性能改进。在本文中，我们将探讨Vue 3中的一些关键特性，以及如何利用它们构建现代化的前端应用。</p>
      
      <h2>Composition API</h2>
      <p>Composition API是Vue 3中最重要的新特性之一。它提供了一种新的组织组件逻辑的方式，特别适合处理复杂组件。通过Composition API，我们可以按照功能领域而不是选项类型来组织代码，这使得代码更加清晰和可重用。</p>
      
      <pre><code>
import { ref, onMounted, watch } from 'vue'

export default {
  setup() {
    // 响应式状态
    const count = ref(0)
    
    // 生命周期钩子
    onMounted(() => {
      console.log('组件已挂载')
    })
    
    // 侦听器
    watch(count, (newValue) => {
      console.log(\`计数已更改为\${newValue}\`)
    })
    
    // 暴露给模板
    return {
      count
    }
  }
}
      </code></pre>
      
      <h2>Teleport</h2>
      <p>Teleport是一个新的内置组件，它允许我们将组件的一部分DOM"传送"到DOM树中的其他位置。这对于创建模态框、弹出窗口和吐司通知特别有用。</p>
      
      <h2>Fragments</h2>
      <p>在Vue 3中，组件可以有多个根节点。这消除了在Vue 2中必须将所有内容包装在单个div中的需求。</p>
      
      <h2>性能优化</h2>
      <p>Vue 3通过重写虚拟DOM实现和优化编译时来提供显著的性能改进。新的编译器可以检测模板中的静态内容并将其提升，减少渲染时的工作量。</p>
      
      <h2>结论</h2>
      <p>Vue 3提供了许多强大的新功能和性能改进，使其成为构建现代化前端应用的绝佳选择。通过采用Composition API、Teleport和Fragments等新特性，我们可以编写更清晰、更可维护和更高效的代码。</p>
    `,
    createdAt: '2023-04-15T10:30:00',
    category: '前端开发',
    author: '张三',
    authorAvatar: 'https://randomuser.me/api/portraits/men/1.jpg',
    coverImage: 'https://picsum.photos/id/0/1200/400',
    tags: ['Vue', 'JavaScript', '前端开发', 'Composition API'],
    likes: 24,
    liked: false,
    comments: [
      {
        name: '李四',
        avatar: 'https://randomuser.me/api/portraits/men/2.jpg',
        date: '2023-04-16T08:12:00',
        content: '写得太好了！我刚开始学习Vue 3，这篇文章对我帮助很大。',
        likes: 5
      },
      {
        name: '王五',
        avatar: 'https://randomuser.me/api/portraits/women/3.jpg',
        date: '2023-04-16T14:30:00',
        content: '请问Composition API与Options API相比，在大型项目中有什么优势？',
        likes: 2
      }
    ]
  },
  {
    id: 2,
    title: 'Spring Boot最佳实践',
    excerpt: '本文将介绍Spring Boot开发中的一些常见问题和解决方案...',
    content: `
      <p>Spring Boot是一个用于简化Spring应用开发的框架。它提供了一种快速创建独立的、基于Spring的生产级应用程序的方法。在本文中，我们将探讨一些Spring Boot开发中的最佳实践。</p>
      
      <h2>合理组织项目结构</h2>
      <p>一个良好的项目结构可以提高代码的可读性和可维护性。一般来说，我们可以按照以下方式组织Spring Boot项目：</p>
      <ul>
        <li>controller：用于存放控制器类</li>
        <li>service：用于存放服务类</li>
        <li>repository：用于存放数据访问类</li>
        <li>model：用于存放实体类</li>
        <li>config：用于存放配置类</li>
        <li>util：用于存放工具类</li>
      </ul>
      
      <h2>使用Spring Boot Starter</h2>
      <p>Spring Boot Starter是一组依赖项，可以方便地添加到项目中。使用这些starter可以减少手动配置的工作量，让我们专注于业务逻辑的开发。</p>
      
      <h2>适当使用Spring Profiles</h2>
      <p>在不同的环境中（如开发、测试、生产），应用程序的配置可能会有所不同。通过使用Spring Profiles，我们可以为不同的环境提供不同的配置。</p>
      
      <h2>优化数据库访问</h2>
      <p>使用Spring Data JPA可以简化数据库访问操作。为了优化性能，我们应该注意以下几点：</p>
      <ul>
        <li>为查询方法添加适当的索引</li>
        <li>避免使用N+1查询</li>
        <li>对于复杂查询，考虑使用原生SQL或QueryDSL</li>
      </ul>
      
      <h2>实现适当的异常处理</h2>
      <p>使用@ControllerAdvice和@ExceptionHandler可以实现全局异常处理，统一处理应用程序中的异常，并返回适当的响应。</p>
      
      <h2>结论</h2>
      <p>遵循这些最佳实践可以帮助我们开发出高质量的Spring Boot应用程序。随着项目的发展，我们应该不断审视和改进代码，确保其质量和可维护性。</p>
    `,
    createdAt: '2023-04-10T14:20:00',
    category: '后端开发',
    author: '李四',
    authorAvatar: 'https://randomuser.me/api/portraits/men/4.jpg',
    coverImage: 'https://picsum.photos/id/1/1200/400',
    tags: ['Spring Boot', 'Java', '后端开发'],
    likes: 18,
    liked: false,
    comments: [
      {
        name: '张三',
        avatar: 'https://randomuser.me/api/portraits/men/1.jpg',
        date: '2023-04-11T09:45:00',
        content: '写得非常实用，特别是关于项目结构的建议。',
        likes: 3
      }
    ]
  },
  {
    id: 3,
    title: '我的旅行日记：杭州西湖',
    excerpt: '上周末去了杭州，游览了风景如画的西湖，体验了当地的文化和美食...',
    content: `
      <p>上周末，我终于实现了长久以来的心愿，前往杭州游览了闻名遐迩的西湖。在这篇文章中，我想分享我在杭州的旅行体验，包括景点、美食和文化。</p>
      
      <h2>西湖美景</h2>
      <p>西湖是杭州的标志性景点，被誉为"人间天堂"。湖水清澈，周围群山环抱，景色如画。我沿着湖边漫步，欣赏了"苏堤春晓"、"平湖秋月"等著名景点。特别是断桥残雪，即使在没有雪的季节，也依然吸引了众多游客。</p>
      
      <h2>灵隐寺</h2>
      <p>除了西湖，我还参观了附近的灵隐寺。这座古老的寺庙坐落在山谷中，环境幽静，古树参天。寺内有许多精美的佛像和建筑，让人感受到浓厚的宗教氛围。</p>
      
      <h2>地道美食</h2>
      <p>杭州的美食也是此行的一大亮点。我品尝了著名的西湖醋鱼、龙井虾仁和东坡肉等当地特色菜肴。此外，我还去了知味观品尝了正宗的杭州小笼包和叫化鸡，味道确实不负盛名。</p>
      
      <h2>文化体验</h2>
      <p>杭州不仅有美丽的自然风光和美食，还有深厚的文化底蕴。我参观了中国丝绸博物馆，了解了杭州作为丝绸之路起点的历史。在河坊街，我看到了许多传统手工艺品，如扇子、丝绸制品和茶叶。</p>
      
      <h2>住宿体验</h2>
      <p>我选择了西湖边的一家精品酒店，从房间可以直接看到湖景。清晨，我坐在阳台上喝着龙井茶，欣赏薄雾笼罩下的西湖，那种宁静与美好难以言表。</p>
      
      <h2>总结</h2>
      <p>这次杭州之行让我深深爱上了这座城市。它完美地融合了自然美景、历史文化和现代都市的元素。如果你还没有去过杭州，我强烈推荐你安排一次旅行，亲身体验这座城市的魅力。</p>
    `,
    createdAt: '2023-04-05T08:15:00',
    category: '生活随笔',
    author: '王五',
    authorAvatar: 'https://randomuser.me/api/portraits/men/5.jpg',
    coverImage: 'https://picsum.photos/id/26/1200/400',
    tags: ['旅行', '杭州', '西湖', '美食'],
    likes: 42,
    liked: false,
    comments: [
      {
        name: '赵六',
        avatar: 'https://randomuser.me/api/portraits/women/6.jpg',
        date: '2023-04-06T10:20:00',
        content: '看了你的文章，我也想去杭州了！请问住宿大概什么价位？',
        likes: 1
      },
      {
        name: '钱七',
        avatar: 'https://randomuser.me/api/portraits/men/7.jpg',
        date: '2023-04-06T15:45:00',
        content: '西湖醋鱼真的很好吃，我去年去的时候特意找了正宗的店家品尝。',
        likes: 3
      },
      {
        name: '孙八',
        avatar: 'https://randomuser.me/api/portraits/women/8.jpg',
        date: '2023-04-07T09:30:00',
        content: '推荐去西湖边看日出，非常美！',
        likes: 7
      }
    ]
  }
]

onMounted(() => {
  fetchPost()
})

const fetchPost = () => {
  loading.value = true
  error.value = null
  
  // 模拟API请求
  setTimeout(() => {
    try {
      const foundPost = mockPosts.find(p => p.id === postId.value)
      
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

const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
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