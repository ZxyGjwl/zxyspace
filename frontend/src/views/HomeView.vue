<template>
  <div class="home" :class="{ 'fade-in': isVisible, 'transition-mode': showStripeTransition }">
    <!-- 使用公共的条带过渡组件 -->
    <StripeTransition 
      :active="showStripeTransition" 
      :revealing="showStripeReveal" 
      :covered="showStripeCovered"
      :visible="stripeVisible" 
    />
    
    <!-- 横向黄色条带过渡效果 -->
    <div v-if="showDiagonalTransition" class="horizontal-transition">
      <div class="horizontal-stripe" v-for="n in 6" :key="n"></div>
    </div>
    
    <!-- 左上角Zxy space文本图标 - 独立于main-content容器 -->
          <AppLogo routeTo="/home" :noMargin="true" />
    
    <!-- 主要内容 - 始终显示，在过渡模式下作为背景 -->
    <div class="main-content">
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
            <button @click="goToAbout" class="btn btn-text">了解更多 <span class="arrow">→</span></button>
          </div>
        </div>
        
        <div class="feature-card" :class="{ 'slide-in': isVisible }" style="--delay: 0.3s">
          <h2>技术栈</h2>
          <div class="tech-tags">
            <span class="tag">Python</span>
            <span class="tag">Java</span>
            <span class="tag">SpringBoot</span>
            <span class="tag">Vue</span>
            <span class="tag">React</span>
            <span class="tag">Docker</span>
            <span class="tag">Node.js</span>
            <span class="tag">云计算</span>
            <span class="tag">Cursor</span>
            <span class="tag">MCP</span>
            <span class="tag">大模型</span>
            <span class="tag">RAG</span>
            <span class="tag">lora微调</span>
            <span class="tag">Deepseek</span>
            <span class="tag">Qwen</span>
            <span class="tag">Claude</span>
            <span class="tag">GPT-4o</span>
            <span class="tag">Git</span>
          </div>
          <div class="card-footer">
            <button @click="goToTechStack" class="btn btn-text">查看详情 <span class="arrow">→</span></button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onActivated, defineExpose, nextTick } from 'vue'
import { RouterLink, useRoute, useRouter } from 'vue-router'
import axios from 'axios'
import StripeTransition from '../components/common/StripeTransition.vue'
import AppLogo from '../components/common/AppLogo.vue'
import { formatDate, mockPosts } from '../utils/common.js'

const route = useRoute()
const router = useRouter()
const recentPosts = ref([])
const loading = ref(true)
const error = ref(null)
let dataLoaded = false
const isVisible = ref(false)
const showStripeReveal = ref(false)
const showStripeTransition = ref(false)
const showStripeCovered = ref(false)
const stripeVisible = ref(false)

// 预加载状态
const preLoaded = ref(false)

// 横向黄色条带过渡效果
const showDiagonalTransition = ref(false)



// 预加载数据方法
const preloadData = () => {
  if (dataLoaded) return Promise.resolve(recentPosts.value)
  
  return new Promise((resolve) => {
    // 直接加载数据，移除假加载效果
    recentPosts.value = mockPosts
    loading.value = false
    dataLoaded = true
    preLoaded.value = true
    
    // 预渲染组件，但在过渡模式下不立即显示
    nextTick(() => {
      // 只在非过渡模式下立即显示内容
      if (route.query.showStripeReveal !== 'true') {
        isVisible.value = true
      }
      // 立即解析Promise
      resolve(recentPosts.value)
    })
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
    
    // 直接加载数据，移除假加载效果
    recentPosts.value = mockPosts
    loading.value = false
    dataLoaded = true
    
    // 只在非过渡模式下显示
    if (route.query.showStripeReveal !== 'true') {
      isVisible.value = true
    }
  } catch (err) {
    error.value = '获取数据失败，请稍后再试'
    loading.value = false
    console.error(err)
  }
}

// 恢复正常布局的方法
const restoreLayout = () => {
  // 移除过渡相关的查询参数，恢复正常布局
  router.replace({
    path: '/home',
    query: {
      // 移除 showStripeReveal 参数，但保留其他可能的参数
      ...Object.fromEntries(
        Object.entries(route.query).filter(([key]) => key !== 'showStripeReveal')
      )
    }
  });
}

const goToAbout = () => {
  // 触发条带过渡动画
  showDiagonalTransition.value = true
  
  // 调整等待时间，匹配新的快速动画速度（0.4s + 0.25s延迟 = 0.65s）
  setTimeout(() => {
    router.push('/about')
  }, 800) // 减少到800ms，让跳转更迅速
}

const goToTechStack = () => {
  // 触发条带过渡动画
  showDiagonalTransition.value = true
  
  // 调整等待时间，匹配新的快速动画速度（0.4s + 0.25s延迟 = 0.65s）
  setTimeout(() => {
    router.push({
      path: '/tech-stack'
    })
  }, 800) // 减少到800ms，让跳转更迅速
}

onMounted(() => {
  // 检查是否需要显示条带消失动画
  if (route.query.showStripeReveal === 'true') {
    // 立即显示条带并设置为覆盖状态，确保无缝衔接
    stripeVisible.value = true
    showStripeCovered.value = true // 立即处于覆盖状态，无闪烁
    showStripeTransition.value = false
    showStripeReveal.value = false
    
    // 预加载数据但不显示首页内容，避免与条带动画冲突
    preloadData()
    
    // 主内容在整个过渡期间保持隐藏，避免闪屏
    isVisible.value = false
    
    // 给条带一个短暂的时间稳定在覆盖状态，然后开始消失动画
    nextTick(() => {
      // 等待一小段时间确保条带完全稳定，然后开始消失动画
      setTimeout(() => {
        showStripeCovered.value = false // 取消静态覆盖状态
        showStripeReveal.value = true // 开始消失动画
      }, 50) // 减少到50ms，更快的过渡
      
      // 在条带消失到一定程度后再显示主内容
      setTimeout(() => {
        isVisible.value = true // 在条带消失动画进行到中期时显示内容
      }, 200) // 减少到200ms，更快显示内容
      
      // 条带消失动画完成后恢复布局
      setTimeout(() => {
        showStripeReveal.value = false // 停止消失动画
        stripeVisible.value = false // 控制条带可见性
        restoreLayout() // 恢复正常布局，导航栏重新显示
      }, 660) // 调整动画时间：50ms延迟 + 0.4s动画 + 0.16s最后条带延迟 = 660ms
    })
  } else {
    // 直接访问时的逻辑
    if (!document.referrer || !document.referrer.includes('splash')) {
      isVisible.value = true
    }
    loadData()
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
  padding-bottom: var(--spacing-lg);
  background-color: var(--background-dark); /* 确保有正确的背景色 */
  
  /* 过渡模式下的样式 */
  &.transition-mode {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    width: 100vw;
    height: 100vh;
    max-width: 100vw;
    padding: 0;
    z-index: 1;
    overflow: hidden; /* 防止内容溢出 */
    background-color: var(--background-dark); /* 确保过渡模式下也有正确背景 */
    
    /* 硬件加速优化 */
    will-change: transform, opacity;
    transform: translateZ(0);
    backface-visibility: hidden;
    contain: layout style paint;
  }
}

/* 主要内容容器 */
.main-content {
  width: 100%;
  height: 100%;
  opacity: 0; /* 默认隐藏，防止闪屏 */
  transition: opacity 0.4s ease-in-out;
  
  /* 性能优化 */
  will-change: opacity;
  transform: translateZ(0);
  backface-visibility: hidden;
}

/* 当组件可见时显示内容 */
.home.fade-in .main-content {
  opacity: 1;
}

/* 左上角logo样式已移至AppLogo组件 */

.hero-section {
  text-align: center;
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md) 0;
  
  h1 {
    margin-bottom: var(--spacing-sm);
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
  gap: var(--spacing-md);
  width: 100%;
  opacity: 1;
  transition: opacity 0.3s ease-in;
}

.feature-card {
  background-color: var(--background-card);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md);
  box-shadow: var(--box-shadow);
  border: 1px solid rgba(255, 255, 255, 0.05);
  transform: translateZ(0) translateY(0);
  opacity: 1;
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1) var(--delay, 0s), 
              opacity 0.3s cubic-bezier(0.22, 1, 0.36, 1) var(--delay, 0s),
              box-shadow 0.3s ease;
  
  /* 性能优化 */
  will-change: transform, box-shadow;
  backface-visibility: hidden;
  contain: layout style;
  
  &:hover {
    box-shadow: 0 12px 28px rgba(0, 0, 0, 0.2);
  }
  
  h2 {
    color: var(--primary-color);
    margin-bottom: var(--spacing-sm);
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
  margin: var(--spacing-xs) 0;
}

.post-item {
  margin-bottom: var(--spacing-xs);
  padding-bottom: var(--spacing-xs);
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
  gap: 2px;
}

.post-meta {
  display: flex;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  color: var(--text-tertiary);
}

.post-category {
  color: var(--primary-color);
  font-weight: var(--font-weight-medium);
}

.post-item h3 {
  color: var(--text-primary);
  margin: 2px 0 4px 0;
  transition: color 0.2s ease;
  font-size: var(--font-size-lg);
  line-height: 1.3;
}

.post-excerpt {
  color: var(--text-secondary);
  line-height: 1.4;
  font-size: var(--font-size-sm);
  margin: 0;
}

.card-footer {
  margin-top: var(--spacing-sm);
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
  line-height: 1.5;
  
  p {
    margin-bottom: var(--spacing-sm);
    
    &:last-child {
      margin-bottom: 0;
    }
  }
}

.tech-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
  margin: var(--spacing-sm) 0;
  
  .tag {
    background-color: rgba(255, 220, 0, 0.1);
    color: var(--primary-color);
    padding: 4px 10px;
    border-radius: 16px;
    font-size: var(--font-size-sm);
    font-weight: var(--font-weight-medium);
    transition: all 0.2s ease;
    border: 1px solid rgba(255, 220, 0, 0.2);
    
    &:hover {
      background-color: rgba(255, 220, 0, 0.2);
      transform: translateY(-2px);
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    }
  }
}

.loading {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: var(--spacing-md) 0;
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
  padding: var(--spacing-sm);
  text-align: center;
  background-color: rgba(255, 69, 58, 0.1);
  border-radius: var(--border-radius-md);
}

@media (min-width: 768px) {
  .home {
    /* 在过渡模式下保持全屏 */
    &:has(.stripe-transition) {
      padding: 0;
    }
  }
  
  /* Logo样式已移至AppLogo组件 */
  
  .hero-section {
    margin-bottom: var(--spacing-xl);
    padding: var(--spacing-lg) 0;
  }
  
  .feature-grid {
    grid-template-columns: 2fr 1fr;
    grid-template-rows: auto auto;
    gap: var(--spacing-lg);
  }
  
  .feature-card {
    padding: var(--spacing-lg);
    
    h2 {
      margin-bottom: var(--spacing-md);
    }
  }
  
  .feature-card:first-child {
    grid-row: span 2;
  }
  
  .post-list {
    margin: var(--spacing-sm) 0;
  }
  
  .post-item {
    margin-bottom: var(--spacing-sm);
    padding-bottom: var(--spacing-sm);
    
    h3 {
      margin: var(--spacing-xs) 0 6px 0;
    }
  }
  
  .post-excerpt {
    font-size: var(--font-size-md);
  }
  
  .card-footer {
    margin-top: var(--spacing-md);
  }
}

@media (min-width: 1024px) {
  .feature-grid {
    grid-template-columns: 1fr 1fr 1fr;
    grid-template-rows: auto;
    gap: var(--spacing-xl);
  }
  
  .feature-card:first-child {
    grid-row: auto;
  }
}

/* 横向黄色条带过渡效果 */
.horizontal-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  pointer-events: none;
  z-index: 99999;
}

.horizontal-stripe {
  position: absolute;
  top: 0;
  width: 100vw;
  height: calc(100vh / 6 + 1px); /* 增加1px避免缝隙 */
  background: var(--primary-color);
  will-change: transform;
  animation: horizontalSlideIn 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; /* 加快到0.4s */
}

/* 左右轴对称进入：前3个条带从左进入，后3个条带从右进入 */
.horizontal-stripe:nth-child(1) { 
  top: 0; 
  transform: translateX(-100vw); /* 从左边进入 */
  animation-delay: 0s; /* 与第6条带同时 */
}
.horizontal-stripe:nth-child(2) { 
  top: calc(100vh / 6 * 1 - 1px);
  transform: translateX(-100vw); /* 从左边进入 */
  animation-delay: 0.05s; /* 与第5条带同时 */
}
.horizontal-stripe:nth-child(3) { 
  top: calc(100vh / 6 * 2 - 1px); 
  transform: translateX(-100vw); /* 从左边进入 */
  animation-delay: 0.10s; /* 与第4条带同时 */
}
.horizontal-stripe:nth-child(4) { 
  top: calc(100vh / 6 * 3 - 1px); 
  transform: translateX(100vw); /* 从右边进入 */
  animation-delay: 0.10s; /* 与第3条带同时 */
}
.horizontal-stripe:nth-child(5) { 
  top: calc(100vh / 6 * 4 - 1px); 
  transform: translateX(100vw); /* 从右边进入 */
  animation-delay: 0.05s; /* 与第2条带同时 */
}
.horizontal-stripe:nth-child(6) { 
  top: calc(100vh / 6 * 5 - 1px); 
  transform: translateX(100vw); /* 从右边进入 */
  animation-delay: 0s; /* 与第1条带同时 */
}

@keyframes horizontalSlideIn {
  0% {
    /* 保持初始位置（在视窗外） */
  }
  100% {
    transform: translateX(0); /* 移动到中间位置，完全铺满屏幕 */
  }
}
</style>