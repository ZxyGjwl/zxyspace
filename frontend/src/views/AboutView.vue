<template>
  <div class="about-timeline" :class="{ 'page-enter': pageVisible }" ref="scrollContainerRef">
    <!-- 黄色条带消失过渡效果 -->
    <div v-if="showStripeExit" class="stripe-exit-transition">
      <div class="exit-stripe" v-for="n in 6" :key="n"></div>
    </div>
    
    <!-- 左上角 Logo -->
    <AppLogo :fixed="true" routeTo="/home" />
    
    <div class="container">
      <!-- 页面标题 -->
      <div class="page-header">
        <h1 class="page-title">我的人生轨迹</h1>
        <p class="page-subtitle">技术路上的每一步足迹</p>
      </div>

      <!-- 时间轴容器 -->
      <div class="timeline-container">
        <div class="timeline-line"></div>
        
        <!-- 时间轴项目 -->
        <div 
          v-for="(item, index) in timelineData" 
          :key="index"
          class="timeline-item"
          :class="[
            `timeline-${item.type}`,
            { 'timeline-left': index % 2 === 0, 'timeline-right': index % 2 === 1 },
            { 'timeline-focus': index === focusCardIndex }
          ]"
        >
          <!-- 时间轴节点 -->
          <div class="timeline-node">
            <div class="timeline-dot" :class="`dot-${item.type}`">
              <i :class="item.icon"></i>
            </div>
            <div class="timeline-date">{{ item.date }}</div>
          </div>

          <!-- 时间轴内容卡片 -->
          <div class="timeline-content">
            <div class="content-card" :class="`card-${item.type}`">
              <div class="card-header">
                <h3 class="card-title">{{ item.title }}</h3>
                <span class="card-type">{{ item.typeLabel }}</span>
              </div>
              
              <div class="card-body">
                <p v-if="item.subtitle" class="card-subtitle">{{ item.subtitle }}</p>
                <p v-if="item.description" class="card-description">{{ item.description }}</p>
                
                <!-- 技能标签 -->
                <div v-if="item.skills && item.skills.length" class="skills-tags">
                  <span 
                    v-for="skill in item.skills" 
                    :key="skill"
                    class="skill-tag"
                  >
                    {{ skill }}
                  </span>
                </div>

                <!-- 成就列表 -->
                <ul v-if="item.achievements && item.achievements.length" class="achievements-list">
                  <li v-for="achievement in item.achievements" :key="achievement">
                    {{ achievement }}
                  </li>
                </ul>

                <!-- 项目链接 -->
                <div v-if="item.links && item.links.length" class="project-links">
                  <a 
                    v-for="link in item.links" 
                    :key="link.name"
                    :href="link.url" 
                    class="project-link"
                    target="_blank"
                  >
                    <i :class="link.icon"></i>
                    {{ link.name }}
                  </a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import AppLogo from '../components/common/AppLogo.vue'

// 页面可见性控制
const pageVisible = ref(false)

// 黄色条带消失控制
const showStripeExit = ref(false)

// 焦点卡片索引
const focusCardIndex = ref(-1)

// 滚动容器引用
const scrollContainerRef = ref(null)

// 节流函数
const throttle = (func, limit) => {
  let inThrottle
  return function() {
    const args = arguments
    const context = this
    if (!inThrottle) {
      func.apply(context, args)
      inThrottle = true
      setTimeout(() => inThrottle = false, limit)
    }
  }
}

// 焦点检测
const detectFocusCard = () => {
  const timelineItems = document.querySelectorAll('.timeline-item')
  
  if (timelineItems.length === 0) {
    return
  }

  const viewportHeight = window.innerHeight
  const viewportCenter = viewportHeight / 2

  let closestIndex = 0
  let closestDistance = Infinity

  timelineItems.forEach((item, index) => {
    const rect = item.getBoundingClientRect()
    const itemCenter = rect.top + rect.height / 2
    const distanceFromCenter = Math.abs(itemCenter - viewportCenter)
    
    if (distanceFromCenter < closestDistance) {
      closestDistance = distanceFromCenter
      closestIndex = index
    }
  })

  if (focusCardIndex.value !== closestIndex) {
    focusCardIndex.value = closestIndex
  }
}

// 节流版本的焦点检测 - 提高刷新率到60fps
const throttledDetectFocus = throttle(detectFocusCard, 16)

// 页面初始化
onMounted(() => {
  // 移除任何残留的条带过渡元素
  const transitionElements = document.querySelectorAll('.side-transition, .diagonal-transition, .horizontal-transition')
  transitionElements.forEach(el => el.remove())
  
  // 立即显示页面内容作为背景
  pageVisible.value = true
  
  // 显示黄色条带（初始状态铺满屏幕）
  showStripeExit.value = true
  
  // 立即添加滚动监听器，不等待动画完成
  nextTick(() => {
    // 使用组件引用获取滚动容器
    if (scrollContainerRef.value) {
      scrollContainerRef.value.addEventListener('scroll', throttledDetectFocus, { passive: true })
    } else {
      window.addEventListener('scroll', throttledDetectFocus, { passive: true })
    }
    // 稍微延迟初始检测，确保DOM完全渲染
    setTimeout(() => {
      detectFocusCard()
    }, 200)
  })
  
  // 短暂延迟后开始条带消失动画
  setTimeout(() => {
    const stripes = document.querySelectorAll('.exit-stripe')
    stripes.forEach((stripe, index) => {
      stripe.classList.add('stripe-disappear')
    })
    
    // 条带消失完成后隐藏条带容器
    setTimeout(() => {
      showStripeExit.value = false
    }, 500)
  }, 100)
})

// 清理事件监听器
onUnmounted(() => {
  if (scrollContainerRef.value) {
    scrollContainerRef.value.removeEventListener('scroll', throttledDetectFocus)
  }
  window.removeEventListener('scroll', throttledDetectFocus)
})

// 时间轴测试数据
const timelineData = ref([
  {
    date: '2024',
    type: 'current',
    typeLabel: '当前状态',
    title: '全栈开发工程师',
    subtitle: '某科技有限公司 · 高级工程师',
    description: '负责公司核心产品的架构设计与开发，带领团队完成多个重要项目，专注于现代化Web应用开发。',
    skills: ['Vue 3', 'Spring Boot', 'Docker', 'Kubernetes', 'AI集成'],
    achievements: [
      '主导公司核心产品重构，性能提升40%',
      '建立前端组件库，提高开发效率30%',
      '引入AI辅助开发工具，优化团队工作流程'
    ],
    icon: 'fas fa-briefcase'
  },
  {
    date: '2023',
    type: 'project',
    typeLabel: '重要项目',
    title: '智能客服系统',
    subtitle: '基于大语言模型的企业级解决方案',
    description: '独立设计并开发了一套智能客服系统，集成了多种AI模型，为企业提供7x24小时智能服务。',
    skills: ['Python', 'FastAPI', 'LangChain', 'Vue.js', 'Redis'],
    achievements: [
      '处理客户咨询效率提升60%',
      '客户满意度从85%提升至95%',
      '为公司节省人力成本100万/年'
    ],
    links: [
      { name: 'GitHub', url: '#', icon: 'fab fa-github' },
      { name: 'Demo', url: '#', icon: 'fas fa-external-link-alt' }
    ],
    icon: 'fas fa-robot'
  },
  {
    date: '2022',
    type: 'skill',
    typeLabel: '技能提升',
    title: '全栈技能扩展',
    subtitle: '从后端向全栈转型的关键一年',
    description: '系统学习前端技术栈，掌握现代化前端开发技能，成功转型为全栈开发工程师。',
    skills: ['Vue.js', 'React', 'TypeScript', 'Webpack', 'Vite'],
    achievements: [
      '完成10+个前端项目实战',
      '获得Vue.js官方认证',
      '在团队内分享前端最佳实践'
    ],
    icon: 'fas fa-code'
  },
  {
    date: '2020-2022',
    type: 'work',
    typeLabel: '工作经历',
    title: '后端开发工程师',
    subtitle: '某网络科技有限公司',
    description: '专注于Java后端开发，参与多个企业级项目的设计与开发，积累了丰富的后端开发经验。',
    skills: ['Java', 'Spring Boot', 'MyBatis', 'MySQL', 'Redis'],
    achievements: [
      '优化核心接口性能，响应时间减少50%',
      '设计并实现微服务架构',
      '建立完善的代码规范和开发流程'
    ],
    icon: 'fas fa-server'
  },
  {
    date: '2019',
    type: 'education',
    typeLabel: '教育背景',
    title: '计算机科学与技术',
    subtitle: '某大学 · 本科学士学位',
    description: '主修计算机科学与技术，专业课程包括数据结构、算法、数据库、操作系统等，为后续技术发展奠定了坚实基础。',
    skills: ['C/C++', 'Java', 'Python', '数据结构', '算法'],
    achievements: [
      '专业成绩排名前10%',
      '获得校级优秀学生奖学金',
      '参与ACM竞赛获得地区三等奖'
    ],
    icon: 'fas fa-graduation-cap'
  },
  {
    date: '2018',
    type: 'start',
    typeLabel: '编程起点',
    title: '初识编程世界',
    subtitle: '人生中的第一行代码',
    description: '在大学计算机课程中第一次接触编程，被代码的魅力深深吸引，从此踏上了技术之路。',
    skills: ['C语言', '基础算法', '计算机基础'],
    achievements: [
      '完成第一个"Hello World"程序',
      '熟练掌握C语言基础语法',
      '确立了技术发展方向'
    ],
    icon: 'fas fa-seedling'
  }
])
</script>

<style scoped lang="scss">
/* 页面推入效果 */
.about-timeline {
  width: 100%;
  min-height: 100vh;
  background-color: var(--background-dark);
  color: var(--text-primary);
  opacity: 0;
  transform: scale(0.95);
  transition: all 0.6s cubic-bezier(0.22, 1, 0.36, 1);
  /* 确保页面可以正常滚动 */
  overflow-x: hidden;
  overflow-y: auto;
  /* 增强滚动体验 */
  scroll-behavior: smooth;
  
  &.page-enter {
    opacity: 1;
    transform: scale(1);
  }
}

/* 自定义滚动条样式 */
.about-timeline::-webkit-scrollbar {
  width: 8px;
}

.about-timeline::-webkit-scrollbar-track {
  background: var(--background-lighter);
  border-radius: 4px;
}

.about-timeline::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-color), #ffd700);
  border-radius: 4px;
  transition: background 0.3s ease;
}

.about-timeline::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, #ffd700, var(--primary-color));
}

/* Firefox 滚动条样式 */
.about-timeline {
  scrollbar-width: thin;
  scrollbar-color: var(--primary-color) var(--background-lighter);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  /* 确保容器有足够的最小高度 */
  min-height: calc(100vh - 4rem);
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 4rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
  background: linear-gradient(135deg, var(--primary-color), #ffd700);
  background-clip: text;
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.page-subtitle {
  font-size: 1.1rem;
  color: var(--text-secondary);
  font-style: italic;
}

/* 时间轴容器 */
.timeline-container {
  position: relative;
  max-width: 1000px;
  margin: 0 auto;
}

/* 时间轴主线 */
.timeline-line {
  position: absolute;
  left: 30px;
  top: 0;
  bottom: 0;
  width: 2px;
  background: linear-gradient(to bottom, 
    var(--primary-color) 0%, 
    #ffd700 50%, 
    var(--primary-color) 100%);
  transform: none;
  z-index: 1;
  /* 移动端也隐藏时间轴线 */
  display: none;
}

/* 时间轴项目 */
.timeline-item {
  position: relative;
  margin-bottom: 8rem;
  display: flex;
  align-items: center;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  /* 默认微虚化效果 - 轻微模糊让非焦点内容退到后景 */
  filter: blur(0.8px);
  opacity: 0.7;
  transform: scale(0.98);
}

/* 焦点卡片移除模糊效果并完全清晰 */
.timeline-item.timeline-focus {
  transform: scale(1.02);
  z-index: 10;
  position: sticky;
  top: 10vh;
  bottom: 10vh;
  /* 移除模糊效果，完全清晰突出 */
  filter: blur(0px);
  opacity: 1;
}

.timeline-item.timeline-focus .content-card {
  box-shadow: 
    0 12px 40px rgba(255, 220, 0, 0.4),
    0 6px 25px rgba(0, 0, 0, 0.5),
    0 0 0 2px rgba(255, 220, 0, 0.3);
  border-color: rgba(255, 220, 0, 0.6);
  background: rgba(26, 27, 30, 1); /* 焦点卡片也设置为不透明背景 */
  background-image: linear-gradient(135deg, 
    rgba(26, 27, 30, 1) 0%, 
    rgba(41, 44, 51, 1) 100%); /* 添加不透明的渐变效果 */
  transform: translateY(-4px);
  transition: all 0.5s cubic-bezier(0.25, 0.46, 0.45, 0.94);
  /* 增强焦点卡片的视觉固定感 */
  backdrop-filter: blur(10px);
  border-width: 2px;
}

.timeline-item.timeline-focus .timeline-dot {
  display: none; /* 确保焦点状态下也隐藏圆形图标 */
}

.timeline-item.timeline-focus .timeline-date {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  color: var(--background-dark);
  transform: rotate(-3deg) scale(1.08);
  box-shadow: 
    0 6px 20px rgba(255, 220, 0, 0.6),
    0 3px 12px rgba(0, 0, 0, 0.4),
    0 0 30px rgba(255, 220, 0, 0.4);
  text-shadow: 
    2px 2px 4px rgba(0, 0, 0, 0.4);
  font-weight: 700;
}

/* 为焦点卡片添加光晕背景 */
.timeline-item.timeline-focus::before {
  content: '';
  position: absolute;
  top: -15px;
  left: -15px;
  right: -15px;
  bottom: -15px;
  background: radial-gradient(
    ellipse at center,
    rgba(255, 220, 0, 0.12) 0%,
    rgba(255, 220, 0, 0.08) 40%,
    rgba(255, 220, 0, 0.03) 70%,
    transparent 85%
  );
  border-radius: 25px;
  animation: focus-glow 3.5s ease-in-out infinite alternate;
  pointer-events: none;
  z-index: -1;
}

/* 焦点光晕动画 */
@keyframes focus-glow {
  0% {
    opacity: 0.4;
    transform: scale(0.96);
  }
  100% {
    opacity: 0.8;
    transform: scale(1.04);
  }
}

/* 左侧布局 */
.timeline-left {
  flex-direction: row;
}

.timeline-left .timeline-content {
  margin-right: 1rem;
  text-align: right;
}

/* 右侧布局 */
.timeline-right {
  flex-direction: row-reverse;
}

.timeline-right .timeline-content {
  margin-left: 1rem;
  text-align: left;
}

/* 时间轴节点 */
.timeline-node {
  position: relative;
  z-index: 3;
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 120px;
}

.timeline-dot {
  display: none; /* 移动端也隐藏圆形图标 */
}

.timeline-date {
  position: absolute;
  bottom: -15px;
  right: -20px;
  font-size: 3rem; /* 移动端适当减小字体 */
  font-weight: 900;
  font-family: 'Impact', 'Arial Black', 'Bebas Neue', sans-serif;
  color: var(--background-dark); /* 改为深色文字 */
  opacity: 1; /* 完全不透明 */
  z-index: 5;
  letter-spacing: -0.05em;
  pointer-events: none;
  /* 添加完整的黄色背景 */
  background: linear-gradient(135deg, var(--primary-color), #ffd700);
  padding: 8px 16px;
  border-radius: 8px;
  text-shadow: 
    1px 1px 2px rgba(0, 0, 0, 0.3);
  box-shadow: 
    0 4px 12px rgba(255, 220, 0, 0.4),
    0 2px 6px rgba(0, 0, 0, 0.3);
  transform: rotate(-3deg); /* 移动端减小旋转角度 */
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

/* 左侧卡片年份显示在右下角 */
.timeline-left .timeline-date {
  right: -20px;
  left: auto;
  bottom: -15px;
  transform: rotate(-3deg);
}

/* 右侧卡片年份显示在左下角 */
.timeline-right .timeline-date {
  left: -20px;
  right: auto;
  bottom: -15px;
  transform: rotate(3deg);
}

/* 焦点状态下年份更炫酷 */
.timeline-item.timeline-focus .timeline-date {
  transform: rotate(-3deg) scale(1.08);
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  box-shadow: 
    0 6px 20px rgba(255, 220, 0, 0.6),
    0 3px 12px rgba(0, 0, 0, 0.4),
    0 0 30px rgba(255, 220, 0, 0.4);
  text-shadow: 
    2px 2px 4px rgba(0, 0, 0, 0.4);
}

.timeline-item.timeline-focus.timeline-right .timeline-date {
  transform: rotate(3deg) scale(1.08);
}

/* 时间轴内容 */
.timeline-content {
  flex: 1;
  max-width: 500px;
}

.content-card {
  background: var(--background-card);
  background-color: rgba(26, 27, 30, 1); /* 设置为不透明背景 */
  border-radius: 12px;
  padding: 1.5rem;
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
  position: relative;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.content-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), #ffd700);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.card-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: var(--text-primary);
  margin: 0;
  flex: 1;
}

.card-type {
  background: var(--primary-color);
  color: var(--background-dark);
  padding: 0.3rem 0.8rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  margin-left: 1rem;
}

.card-subtitle {
  color: var(--text-secondary);
  font-weight: 500;
  margin-bottom: 0.8rem;
  font-style: italic;
}

.card-description {
  color: var(--text-primary);
  line-height: 1.6;
  margin-bottom: 1rem;
}

/* 技能标签 */
.skills-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.skill-tag {
  background: var(--background-lighter);
  color: var(--text-primary);
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.8rem;
  border: 1px solid var(--border-color);
}

.skill-tag:hover {
  background: var(--primary-color);
  color: var(--background-dark);
}

/* 成就列表 */
.achievements-list {
  list-style: none;
  padding: 0;
  margin-bottom: 1rem;
}

.achievements-list li {
  position: relative;
  padding-left: 1.5rem;
  margin-bottom: 0.5rem;
  color: var(--text-primary);
}

.achievements-list li::before {
  content: '✨';
  position: absolute;
  left: 0;
  color: var(--primary-color);
}

/* 项目链接 */
.project-links {
  display: flex;
  gap: 1rem;
}

.project-link {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  color: var(--primary-color);
  text-decoration: none;
  padding: 0.5rem 1rem;
  border: 1px solid var(--primary-color);
  border-radius: 8px;
  font-size: 0.9rem;
}

.project-link:hover {
  background: var(--primary-color);
  color: var(--background-dark);
}

/* 黄色条带消失过渡效果 */
.stripe-exit-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  pointer-events: none;
  z-index: 99999;
}

.exit-stripe {
  position: absolute;
  top: 0;
  width: 100vw;
  height: calc(100vh / 6 + 1px); /* 增加1px避免缝隙 */
  background: var(--primary-color);
  will-change: transform;
  transform: translateX(0); /* 初始状态：铺满屏幕 */
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94); /* 加快到0.4s */
}

/* 条带位置 */
.exit-stripe:nth-child(1) { 
  top: 0; 
}
.exit-stripe:nth-child(2) { 
  top: calc(100vh / 6 * 1 - 1px); /* 减少1px避免缝隙 */
}
.exit-stripe:nth-child(3) { 
  top: calc(100vh / 6 * 2 - 1px); 
}
.exit-stripe:nth-child(4) { 
  top: calc(100vh / 6 * 3 - 1px); 
}
.exit-stripe:nth-child(5) { 
  top: calc(100vh / 6 * 4 - 1px); 
}
.exit-stripe:nth-child(6) { 
  top: calc(100vh / 6 * 5 - 1px); 
}

/* 条带消失动画 - 左右轴对称：前3个条带向左消失，后3个条带向右消失 */
.exit-stripe.stripe-disappear:nth-child(1) {
  transform: translateX(-100vw); /* 向左消失 */
  transition-delay: 0s; /* 与第6条带同时 */
}

.exit-stripe.stripe-disappear:nth-child(2) {
  transform: translateX(-100vw); /* 向左消失 */
  transition-delay: 0.05s; /* 与第5条带同时 */
}

.exit-stripe.stripe-disappear:nth-child(3) {
  transform: translateX(-100vw); /* 向左消失 */
  transition-delay: 0.10s; /* 与第4条带同时 */
}

.exit-stripe.stripe-disappear:nth-child(4) {
  transform: translateX(100vw); /* 向右消失 */
  transition-delay: 0.10s; /* 与第3条带同时 */
}

.exit-stripe.stripe-disappear:nth-child(5) {
  transform: translateX(100vw); /* 向右消失 */
  transition-delay: 0.05s; /* 与第2条带同时 */
}

.exit-stripe.stripe-disappear:nth-child(6) {
  transform: translateX(100vw); /* 向右消失 */
  transition-delay: 0s; /* 与第1条带同时 */
}

/* Logo 样式已移至AppLogo组件 */

/* 响应式设计 */
@media (min-width: 768px) {
  /* Logo样式已移至AppLogo组件 */
}

@media (max-width: 768px) {
  .container {
    padding: 1rem;
    /* 移动端确保有足够的滚动空间 */
    min-height: calc(100vh - 2rem);
  }
  
  /* 移动端滚动条样式调整 */
  .about-timeline::-webkit-scrollbar {
    width: 6px;
  }
  
  /* 移动端 Logo 样式已移至AppLogo组件 */
  
  .page-title {
    font-size: 2rem;
  }
  
  .timeline-item {
    flex-direction: row !important;
    margin-bottom: 2rem;
  }
  
  .timeline-left .timeline-content,
  .timeline-right .timeline-content {
    margin: 0 0 0 2rem;
    text-align: left;
    max-width: none;
  }
  
  .timeline-node {
    min-width: 80px;
    position: absolute;
    left: 0;
  }
  
  .timeline-dot {
    width: 40px;
    height: 40px;
    font-size: 1rem;
  }
  
  .timeline-date {
    font-size: 2.2rem; /* 小屏幕进一步减小字体 */
    bottom: -10px;
    right: -15px;
    transform: rotate(-2deg); /* 小屏幕最小旋转角度 */
    padding: 6px 12px; /* 移动端调整内边距 */
  }
  
  /* 小屏幕年份位置调整 */
  .timeline-left .timeline-date,
  .timeline-right .timeline-date {
    right: -15px;
    left: auto;
    bottom: -10px;
    transform: rotate(-2deg);
  }
  
  /* 小屏幕焦点状态 */
  .timeline-item.timeline-focus .timeline-date {
    transform: rotate(-2deg) scale(1.05);
  }
  
  .content-card {
    margin-left: 4rem;
  }
  
  .card-header {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .card-type {
    margin: 0.5rem 0 0 0;
  }
  
  /* Logo动画样式已移至AppLogo组件 */
  
  /* 移动端焦点效果优化 */
  .timeline-item.timeline-focus {
    transform: scale(1.01); /* 小屏幕设备最小缩放 */
    filter: blur(0px);
    opacity: 1;
    position: sticky;
    top: 5vh; /* 小屏幕进一步减少固定区域 */
    bottom: 5vh;
  }
  
  .timeline-item.timeline-focus .timeline-dot {
    transform: scale(1.1); /* 移动端减小节点缩放 */
    animation-duration: 3s; /* 稍微放慢动画 */
  }
  
  .timeline-item.timeline-focus .content-card {
    transform: translateY(-2px); /* 移动端减小上浮效果 */
    box-shadow: 
      0 8px 30px rgba(255, 220, 0, 0.3),
      0 4px 18px rgba(0, 0, 0, 0.4),
      0 0 0 2px rgba(255, 220, 0, 0.25);
  }
  
  /* 移动端减弱光晕效果 */
  .timeline-item.timeline-focus::before {
    opacity: 0.6;
    animation-duration: 4s; /* 移动端稍微放慢动画 */
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0.5rem;
    /* 小屏幕更紧凑的间距 */
    min-height: calc(100vh - 1rem);
  }
  
  /* 小屏幕滚动条进一步优化 */
  .about-timeline::-webkit-scrollbar {
    width: 4px;
  }
  
  /* 小屏幕 Logo 样式已移至AppLogo组件 */
  
  .timeline-dot {
    width: 35px;
    height: 35px;
    font-size: 0.9rem;
  }
  
  .content-card {
    padding: 1rem;
    margin-left: 3rem;
  }
  
  .card-title {
    font-size: 1.1rem;
  }
  
  .project-links {
    flex-direction: column;
  }
  
  /* Logo动画样式已移至AppLogo组件 */
  
  /* 小屏幕焦点效果进一步优化 */
  .timeline-item.timeline-focus {
    transform: scale(1.01); /* 小屏幕进一步减小缩放 */
    filter: blur(0px);
    opacity: 1;
  }
  
  .timeline-item.timeline-focus .timeline-dot {
    transform: scale(1.08); /* 小屏幕进一步减小节点缩放 */
    animation-duration: 3.5s; /* 小屏幕进一步放慢动画 */
  }
  
  .timeline-item.timeline-focus .content-card {
    transform: translateY(-1px); /* 小屏幕减小上浮效果 */
    box-shadow: 
      0 6px 24px rgba(255, 220, 0, 0.25),
      0 3px 15px rgba(0, 0, 0, 0.35),
      0 0 0 1px rgba(255, 220, 0, 0.2);
  }
  
  /* 小屏幕进一步减弱光晕效果 */
  .timeline-item.timeline-focus::before {
    opacity: 0.4;
    animation-duration: 5s; /* 小屏幕进一步放慢动画 */
  }
}

/* 高DPI屏幕和缩放优化 */
@media (-webkit-min-device-pixel-ratio: 2), (min-resolution: 192dpi) {
  .about-timeline::-webkit-scrollbar {
    width: 10px;
  }
  
  .about-timeline::-webkit-scrollbar-thumb {
    border-radius: 5px;
  }
}

/* 确保在任何缩放级别下都能滚动 */
@media (min-height: 600px) {
  .about-timeline {
    /* 确保页面内容始终可滚动 */
    height: 100vh;
    overflow-y: auto;
  }
}

/* 解决iOS Safari滚动问题 */
@supports (-webkit-overflow-scrolling: touch) {
  .about-timeline {
    -webkit-overflow-scrolling: touch;
  }
}

/* 为焦点卡片添加固定状态指示器 */
.timeline-item.timeline-focus::after {
  content: '';
  position: absolute;
  left: -8px;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 60%;
  background: linear-gradient(to bottom, 
    transparent 0%, 
    var(--primary-color) 20%, 
    #ffd700 50%, 
    var(--primary-color) 80%, 
    transparent 100%);
  border-radius: 2px;
  animation: focus-indicator 2s ease-in-out infinite alternate;
  z-index: 15;
}

/* 焦点指示器动画 */
@keyframes focus-indicator {
  0% {
    opacity: 0.6;
    transform: translateY(-50%) scaleY(0.8);
  }
  100% {
    opacity: 1;
    transform: translateY(-50%) scaleY(1);
  }
}
</style>