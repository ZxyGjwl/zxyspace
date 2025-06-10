<template>
  <div class="splash-view">
    <!-- 背景动画效果 -->
    <div class="tech-grid" :style="{ opacity: 1 - dragProgress * 0.5 }"></div>
    
    <!-- 技术栈轮盘 -->
    <div class="tech-orbit" :style="{ opacity: 1 - dragProgress * 1.2 }">
      <div 
        v-for="(tech, index) in techStack" 
        :key="tech" 
        class="tech-item"
        :style="{ 
          transform: `rotate(${getOrbitPosition(index)}deg) translateX(${orbitRadius}px) rotate(-${getOrbitPosition(index)}deg)`,
          opacity: getTechOpacity(index)
        }"
      >
        {{ tech }}
      </div>
    </div>
    
    <!-- 主标语 -->
    <div class="slogan-container" :style="{ transform: `translateY(${-dragProgress * 40}vh)`, opacity: 1 - dragProgress * 1.2 }">
      <div class="logo-container">
        <h1 class="logo cyberpunk-text">Zxy <span>Space</span></h1>
      </div>
      
      <div class="slogan">
        <div class="slogan-line slogan-chinese">
          <div class="slogan-word" 
               v-for="(word, index) in chineseSlogan" 
               :key="`cn-${index}`"
               :class="{ 'visible': wordVisibility[`cn-${index}`] }"
               :style="{ 
                 transform: wordVisibility[`cn-${index}`] ? `translateY(${-dragProgress * 30}px)` : 'translateY(20px)',
               }">
            {{ word }}
          </div>
        </div>
        
        <div class="slogan-line slogan-english">
          <div class="slogan-word" 
               v-for="(word, index) in englishSlogan" 
               :key="`en-${index}`"
               :class="{ 'visible': wordVisibility[`en-${index}`] }"
               :style="{ 
                 transform: wordVisibility[`en-${index}`] ? `translateY(${-dragProgress * 20}px)` : 'translateY(20px)',
               }">
            {{ word }}
          </div>
        </div>
      </div>
    </div>
    
    <!-- 点击进入按钮 -->
    <div class="click-button" @click="handleClick" :style="{ 
      opacity: Math.max(0, 1 - dragProgress * 2),
      transform: `translateY(${-dragProgress * 40}vh)`
    }">
      <div class="click-icon">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 5L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M5 12L12 5L19 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>
    
    <!-- 使用公共的条带过渡组件 -->
    <StripeTransition 
      :active="transitionActive" 
      :visible="transitionActive"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, shallowRef, nextTick, reactive } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import StripeTransition from '../components/common/StripeTransition.vue';
import { PerformanceMonitor } from '../utils/performanceMonitor.js';

const router = useRouter();
const route = useRoute();
const dragProgress = ref(0);
const transitionActive = ref(false);
const homeViewRef = shallowRef(null);
const preloadStarted = ref(false);
const homeViewLoaded = ref(false);
const animationStarted = ref(false);
const homePreviewVisible = ref(false);
const stripePhase = ref('none'); // 'covering', 'revealing', 'none'

// 鼠标位置跟踪
const mouseX = ref(window.innerWidth / 2);
const mouseY = ref(window.innerHeight / 2);
const orbitRotation = ref(0);
const orbitRadius = 180; // 轮盘半径
const orbitSpeed = 0.3; // 增加轮盘旋转速度，使旋转效果更明显
let orbitAnimationFrame = null;
let lastUpdateTime = 0; // 添加时间控制变量
const updateInterval = 1000 / 30; // 限制更新频率为30fps

// 技术栈
const techStack = [
  'Python', 'Java', 'SpringBoot', 'Vue', 'React', 
  'Docker', 'Node.js', '域名', '云计算', 'Cursor', 
  'MCP', '大模型', 'RAG', 'lora微调', 'Deepseek', 
  'Qwen', 'Claude 3.7', 'Gpt-4o', 'Git'
];

// 中英文标语
const chineseSlogan = '生活就是不断学习和挑战'.split('');
const englishSlogan = 'LIFE IS CONTINUOUS LEARNING AND CHALLENGE'.split(' ');

// 控制每个文字的可见性
const wordVisibility = reactive({});

// 初始化所有文字为不可见
const initWordVisibility = () => {
  chineseSlogan.forEach((_, index) => {
    wordVisibility[`cn-${index}`] = false;
  });
  
  englishSlogan.forEach((_, index) => {
    wordVisibility[`en-${index}`] = false;
  });
};

// 计算轮盘中每个技术项的位置
const getOrbitPosition = (index) => {
  const baseAngle = (index * (360 / techStack.length));
  return (baseAngle + orbitRotation.value) % 360;
};

// 计算技术项的透明度，使前方的项目更加突出
const getTechOpacity = (index) => {
  const position = getOrbitPosition(index);
  // 前方位置（0度附近）透明度最高
  return 0.3 + 0.7 * Math.cos((position - 90) * Math.PI / 180);
};

// 更新轮盘旋转
const updateOrbit = () => {
  // 使用时间间隔控制更新频率
  const currentTime = performance.now();
  if (currentTime - lastUpdateTime < updateInterval) {
    orbitAnimationFrame = requestAnimationFrame(updateOrbit);
    return;
  }
  lastUpdateTime = currentTime;

  // 匀速旋转，不受鼠标位置影响
  orbitRotation.value = (orbitRotation.value + orbitSpeed) % 360;
  
  // 使用 requestAnimationFrame 的返回值，便于后续取消
  orbitAnimationFrame = requestAnimationFrame(updateOrbit);
};

// 预加载HomeView内容
const preloadHomeViewContent = () => {
  if (preloadStarted.value) return;
  preloadStarted.value = true;
  
  // 预加载数据
  nextTick(() => {
    if (homeViewRef.value && homeViewRef.value.preloadData) {
      homeViewRef.value.preloadData().then(() => {
        // 标记HomeView已加载完成
        homeViewLoaded.value = true;
      });
    }
  });
};

// 处理点击事件
const handleClick = () => {
  if (transitionActive.value) return; // 防止重复点击
  
  // 开始预加载
  preloadHomeViewContent();
  
  // 开始条带过渡动画
  startStripeTransition();
};

// 开始条带过渡动画
const startStripeTransition = () => {
  transitionActive.value = true;
  stripePhase.value = 'covering';
  
  // 开发环境下监测动画性能
  if (import.meta.env.MODE === 'development') {
    PerformanceMonitor.monitorTransition(null, 1000).then(result => {
      console.log('条带过渡动画性能报告:', result);
    });
  }
  
  // 条带覆盖完成后立即跳转到主页
  // 优化后动画：0.35s动画 + 0.15s最后一个条带延迟 = 0.5s，精确控制跳转时机
  setTimeout(() => {
    jumpToHome();
  }, 520); // 减少到520ms，确保在条带完全覆盖后立即跳转，避免延迟过长
};

// 完成过渡并跳转到主页
const completeTransition = () => {
  // 此函数现在由 startStripeTransition 替代
  startStripeTransition();
};

// 跳转到主页的函数
const jumpToHome = () => {
  // 跳转到主页，传递条带动画状态
  router.push({ 
    path: '/home',
    query: { 
      fromSplash: 'true',
      showStripeReveal: 'true' // 告诉主页展示条带消失动画
    }
  });
};

// 逐个显示文字的函数
const showWordsSequentially = () => {
  if (animationStarted.value) return;
  animationStarted.value = true;
  
  // 先重置所有文字的可见性
  initWordVisibility();
  
  // 逐个显示中文文字
  chineseSlogan.forEach((_, index) => {
    setTimeout(() => {
      wordVisibility[`cn-${index}`] = true;
    }, 500 + index * 100); // 页面加载后500ms开始，每个字间隔100ms
  });
  
  // 逐个显示英文文字，在中文之后
  const chineseDelay = 500 + chineseSlogan.length * 100 + 300; // 中文完成后加300ms延迟
  englishSlogan.forEach((_, index) => {
    setTimeout(() => {
      wordVisibility[`en-${index}`] = true;
    }, chineseDelay + index * 100); // 每个词间隔100ms
  });
};

onMounted(() => {
  // 检查是否需要淡入效果
  if (route.query.fadeIn === 'true') {
    // 从home页面跳转过来，需要淡入效果
    const splashElement = document.querySelector('.splash-view');
    if (splashElement) {
      splashElement.style.opacity = '0';
      splashElement.style.transition = 'opacity 0.6s ease-in';
      
      // 短暂延迟后开始淡入
      setTimeout(() => {
        splashElement.style.opacity = '1';
      }, 100);
    }
    
    // 清理查询参数
    router.replace({ path: '/', query: {} });
  }
  
  // 初始化所有文字为不可见
  initWordVisibility();
  
  // 预渲染HomeView，提前加载资源
  router.isReady().then(() => {
    // 预热路由组件
    router.resolve('/home');
    
    // 页面加载后预加载HomeView内容
    setTimeout(() => {
      preloadHomeViewContent();
    }, 800);
  });
  
  // 页面加载后开始文字动画
  setTimeout(showWordsSequentially, 100);
  
  // 开始轮盘动画
  lastUpdateTime = performance.now(); // 初始化时间戳
  updateOrbit();
});

onUnmounted(() => {
  // 取消轮盘动画
  if (orbitAnimationFrame) {
    cancelAnimationFrame(orbitAnimationFrame);
  }
});
</script>

<style lang="scss" scoped>
.splash-view {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  background-color: var(--background-dark);
  z-index: 1000;
  user-select: none; /* 禁止文本选择 */
  -webkit-user-select: none; /* Safari */
  -moz-user-select: none; /* Firefox */
  -ms-user-select: none; /* IE10+/Edge */
  font-weight: bold; /* 加粗所有文本 */
}

/* 阻止默认滚动行为 */
body {
  overflow: hidden;
}

/* 背景动画 */
.tech-grid {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(255, 220, 0, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 220, 0, 0.03) 1px, transparent 1px);
  background-size: 40px 40px;
  z-index: -1;
}

/* 技术栈轮盘 */
.tech-orbit {
  position: absolute;
  top: 50%;
  left: 50%;
  width: 0;
  height: 0;
  transform: translate(-50%, -50%);
  z-index: 1;
  pointer-events: none; /* 确保不会干扰拖拽操作 */
}

.tech-item {
  position: absolute;
  transform-origin: center;
  background-color: rgba(255, 220, 0, 0.1);
  color: var(--primary-color);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  white-space: nowrap;
  box-shadow: 0 0 15px rgba(255, 220, 0, 0.2);
  border: 1px solid rgba(255, 220, 0, 0.2);
  will-change: transform, opacity;
  backface-visibility: hidden; /* 减少绘制 */
  transform-style: preserve-3d; /* 启用3D加速 */
  -webkit-font-smoothing: antialiased; /* 字体渲染优化 */
}

/* 标语容器 */
.slogan-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-xl);
  will-change: transform, opacity;
}

.logo-container {
  margin-bottom: var(--spacing-lg);
  position: relative;
}

.logo {
  font-size: 4.5rem; /* 放大字体大小 */
  font-weight: 900; /* 更粗的字体 */
  font-style: italic; /* logo斜体 */
  color: var(--primary-color);
  letter-spacing: -0.02em;
  position: relative;
  
  span {
    font-weight: 800;
    font-style: italic; /* logo span斜体 */
    opacity: 0.9;
  }
}

.slogan {
  text-align: center;
}

.slogan-line {
  display: flex;
  justify-content: center;
  margin-bottom: var(--spacing-md);
}

.slogan-chinese {
  font-size: 1.8rem;
  font-weight: var(--font-weight-medium);
}

.slogan-english {
  font-size: 1rem;
  font-weight: var(--font-weight-normal);
  color: var(--text-secondary);
  letter-spacing: 0.05em;
}

.slogan-word {
  display: inline-block;
  margin: 0 2px;
  color: var(--text-primary);
  font-style: italic; /* 标语文字斜体 */
  opacity: 0; /* 默认不可见 */
  transform: translateY(20px); /* 移除斜体效果 */
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1),
              opacity 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform, opacity;
  
  &.visible {
    opacity: 1;
    transform: translateY(0); /* 移除斜体效果 */
  }
}

/* 点击按钮 */
.click-button {
  position: absolute;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 220, 0, 0.1);
  border-radius: 50%;
  will-change: opacity, transform;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  box-shadow: 
    0 0 20px rgba(255, 220, 0, 0.3),
    0 0 40px rgba(255, 220, 0, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  
  &:hover {
    transform: translateX(-50%) scale(1.3);
    background: rgba(255, 220, 0, 0.3);
    box-shadow: 
      0 0 60px rgba(255, 220, 0, 0.8),
      0 0 120px rgba(255, 220, 0, 0.4),
      0 0 180px rgba(255, 220, 0, 0.2),
      inset 0 0 40px rgba(255, 220, 0, 0.2);
    animation: pulse 1.5s infinite ease-in-out;
  }
  
  &:active {
    transform: translateX(-50%) scale(1.1);
    box-shadow: 
      0 0 40px rgba(255, 220, 0, 0.9),
      0 0 80px rgba(255, 220, 0, 0.5),
      inset 0 0 30px rgba(255, 220, 0, 0.3);
  }
}

.click-icon {
  color: var(--primary-color);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  filter: drop-shadow(0 0 8px rgba(255, 220, 0, 0.3));
}

.click-button:hover .click-icon {
  color: rgba(255, 255, 255, 0.95);
  transform: translateY(-5px) rotate(15deg);
  filter: drop-shadow(0 0 20px rgba(255, 220, 0, 0.8));
  animation: iconBounce 1.5s infinite ease-in-out;
}

/* 脉冲动画 */
@keyframes pulse {
  0%, 100% {
    box-shadow: 
      0 0 60px rgba(255, 220, 0, 0.8),
      0 0 120px rgba(255, 220, 0, 0.4),
      0 0 180px rgba(255, 220, 0, 0.2),
      inset 0 0 40px rgba(255, 220, 0, 0.2);
  }
  50% {
    box-shadow: 
      0 0 80px rgba(255, 220, 0, 1),
      0 0 160px rgba(255, 220, 0, 0.6),
      0 0 240px rgba(255, 220, 0, 0.3),
      inset 0 0 60px rgba(255, 220, 0, 0.3);
  }
}

/* 图标弹跳动画 */
@keyframes iconBounce {
  0%, 100% {
    transform: translateY(-5px) rotate(15deg) scale(1);
  }
  50% {
    transform: translateY(-8px) rotate(15deg) scale(1.1);
  }
}

/* 撕裂效果替代阴影 */
.cyberpunk-text {
  position: relative;
  animation: textGlitch 8s infinite alternate;
  
  &::before, &::after {
    content: 'Zxy Space';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    clip-path: polygon(
      0% 0%, 100% 0%, 100% 33%, 
      70% 33%, 75% 38%, 100% 38%, 
      100% 65%, 55% 65%, 60% 72%, 
      100% 72%, 100% 100%, 0% 100%
    );
  }
  
  &::before {
    color: #0ff;
    z-index: -1;
    left: -3px;
    animation: crack-anim-1 5s infinite linear alternate-reverse;
  }
  
  &::after {
    color: #f0f;
    z-index: -2;
    left: 3px;
    animation: crack-anim-2 3s infinite linear alternate-reverse;
  }
}

@keyframes textGlitch {
  0%, 5%, 10%, 15%, 50%, 55%, 60%, 100% {
    opacity: 1;
    filter: brightness(1);
    transform: translateY(0) scale(1);
  }
  
  1%, 6%, 11%, 51%, 56% {
    opacity: 0.8;
    filter: brightness(1.5) contrast(1.5);
    transform: translateY(-1px) scale(1.01);
  }
  
  2%, 7%, 12%, 52%, 57% {
    filter: brightness(0.9) contrast(1.2);
    transform: translateY(1px) scale(0.99);
  }
}

@keyframes crack-anim-1 {
  0%, 100% { 
    transform: translate(0);
    clip-path: polygon(
      0% 0%, 100% 0%, 100% 33%, 
      70% 33%, 75% 38%, 100% 38%, 
      100% 65%, 55% 65%, 60% 72%, 
      100% 72%, 100% 100%, 0% 100%
    );
  }
  
  20%, 80% {
    transform: translate(-2px, 1px);
    clip-path: polygon(
      0% 0%, 100% 0%, 100% 25%, 
      75% 25%, 80% 30%, 100% 30%, 
      100% 70%, 60% 70%, 65% 75%, 
      100% 75%, 100% 100%, 0% 100%
    );
  }
  
  40%, 60% {
    transform: translate(2px, -1px);
    clip-path: polygon(
      0% 0%, 100% 0%, 100% 40%, 
      65% 40%, 70% 45%, 100% 45%, 
      100% 60%, 50% 60%, 55% 65%, 
      100% 65%, 100% 100%, 0% 100%
    );
  }
}

@keyframes crack-anim-2 {
  0%, 100% { 
    transform: translate(0);
    clip-path: polygon(
      0% 0%, 100% 0%, 100% 45%, 
      60% 45%, 65% 50%, 100% 50%, 
      100% 75%, 40% 75%, 45% 80%, 
      100% 80%, 100% 100%, 0% 100%
    );
  }
  
  25%, 75% {
    transform: translate(2px, -1px);
    clip-path: polygon(
      0% 0%, 100% 0%, 100% 35%, 
      65% 35%, 70% 40%, 100% 40%, 
      100% 80%, 45% 80%, 50% 85%, 
      100% 85%, 100% 100%, 0% 100%
    );
  }
  
  50% {
    transform: translate(-2px, 1px);
    clip-path: polygon(
      0% 0%, 100% 0%, 100% 55%, 
      55% 55%, 60% 60%, 100% 60%, 
      100% 70%, 35% 70%, 40% 75%, 
      100% 75%, 100% 100%, 0% 100%
    );
  }
}

/* 赛博朋克闪烁效果 */
@keyframes cyberGlitch {
  0%, 100% {
    opacity: 0.8;
    filter: hue-rotate(0deg);
  }
  25% {
    opacity: 0.6;
    filter: hue-rotate(90deg);
  }
  50% {
    opacity: 1;
    filter: hue-rotate(180deg);
  }
  75% {
    opacity: 0.7;
    filter: hue-rotate(270deg);
  }
}

/* 数字雨动画 */
@keyframes digitalRain {
  0% {
    transform: translateY(0);
  }
  100% {
    transform: translateY(20px);
  }
}

/* 响应式调整 */
@media (max-width: 768px) {
  .logo {
    font-size: 3.5rem; /* 调整移动端大小 */
  }
  
  .slogan-chinese {
    font-size: 1.5rem;
  }
  
  .slogan-english {
    font-size: 0.9rem;
  }
  
  .tech-item {
    font-size: 12px;
    padding: 6px 12px;
  }
}
</style>