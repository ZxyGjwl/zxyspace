<template>
  <div class="splash-view" @mousedown="startDrag" @mousemove="handleMouseMove" @mouseup="endDrag" @mouseleave="endDrag">
    <!-- 背景动画效果 -->
    <div class="tech-grid" :style="{ opacity: 1 - dragProgress * 0.5 }"></div>
    <div class="particles">
      <div v-for="n in 30" :key="n" class="particle" 
          :style="{ 
            top: `${Math.random() * 100 - dragProgress * (n % 5) * 5}%`, 
            left: `${Math.random() * 100 + dragProgress * (n % 3) * 3}%`,
            width: `${Math.random() * 3 + 1}px`,
            height: `${Math.random() * 3 + 1}px`,
            opacity: `${0.3 - dragProgress * 0.15}`
          }">
      </div>
    </div>
    
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
        <h1 class="logo">ZXY <span>Space</span></h1>
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
    
    <!-- 上拉提示 -->
    <div class="drag-hint" :style="{ opacity: Math.max(0, 1 - dragProgress * 2), transform: `translateY(${dragProgress * 20}px)` }">
      <div class="drag-text">向上拖动进入</div>
      <div class="drag-arrow">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M12 5L12 19" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          <path d="M5 12L12 5L19 12" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
        </svg>
      </div>
    </div>
    
    <!-- 预览内容 -->
    <div class="home-preview" :class="{ 'transitioning': transitionActive }" :style="{ transform: `translate3d(0, ${100 - dragProgress * 100}vh, 0)` }">
      <div class="home-preview-container">
        <HomeView ref="homeViewRef" />
      </div>
    </div>
    
    <!-- 进度指示器 -->
    <div class="progress-indicator" :style="{ opacity: 0.3 + dragProgress * 0.7 }">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: `${dragProgress * 100}%` }"></div>
      </div>
      <div class="progress-text">{{ Math.floor(dragProgress * 100) }}%</div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, shallowRef, nextTick, reactive } from 'vue';
import { useRouter } from 'vue-router';
import HomeView from './HomeView.vue';

const router = useRouter();
const isDragging = ref(false);
const startY = ref(0);
const dragY = ref(0);
const dragProgress = ref(0);
const transitionActive = ref(false);
const homeViewRef = shallowRef(null);
const preloadStarted = ref(false);
const homeViewLoaded = ref(false);
const animationStarted = ref(false);

// 鼠标位置跟踪
const mouseX = ref(window.innerWidth / 2);
const mouseY = ref(window.innerHeight / 2);
const orbitRotation = ref(0);
const orbitRadius = 180; // 轮盘半径
const orbitSpeed = 0.05; // 轮盘旋转速度
let orbitAnimationFrame = null;

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

// 处理鼠标移动
const handleMouseMove = (e) => {
  // 更新鼠标位置
  mouseX.value = e.clientX;
  mouseY.value = e.clientY;
  
  // 如果正在拖动，调用拖动处理函数
  if (isDragging.value) {
    dragMove(e);
  }
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
  // 根据鼠标位置计算旋转速度和方向
  const centerX = window.innerWidth / 2;
  const centerY = window.innerHeight / 2;
  const deltaX = mouseX.value - centerX;
  const deltaY = mouseY.value - centerY;
  
  // 计算鼠标到中心的距离，影响旋转速度
  const distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
  const maxDistance = Math.max(window.innerWidth, window.innerHeight) / 2;
  const speedFactor = Math.min(distance / maxDistance, 1) * orbitSpeed;
  
  // 根据鼠标位置计算旋转方向
  const angle = Math.atan2(deltaY, deltaX);
  orbitRotation.value += speedFactor * 5;
  
  // 循环动画
  orbitAnimationFrame = requestAnimationFrame(updateOrbit);
};

// 预加载HomeView内容
const preloadHomeViewContent = () => {
  if (preloadStarted.value) return;
  preloadStarted.value = true;
  
  // 当用户开始拖动时就预加载数据
  nextTick(() => {
    if (homeViewRef.value && homeViewRef.value.preloadData) {
      homeViewRef.value.preloadData().then(() => {
        // 标记HomeView已加载完成
        homeViewLoaded.value = true;
      });
    }
  });
};

// 更强的缓动效果，用于标语移动
function easeOutExpo(x) {
  return x === 1 ? 1 : 1 - Math.pow(2, -10 * x);
}

// 线性函数，用于直接映射拖动距离
function linear(x) {
  return x;
}

// 轻微的缓动效果，保持一定的平滑度但不会明显延迟
function easeOutLight(x) {
  return 1 - Math.pow(1 - x, 1.5); // 使用较小的指数，减弱缓动效果
}

// 处理拖拽开始
const startDrag = (e) => {
  isDragging.value = true;
  startY.value = e.clientY || e.touches?.[0]?.clientY || 0;
  transitionActive.value = false;
  
  // 触发浏览器渲染层的分离，优化性能
  const homePreview = document.querySelector('.home-preview');
  if (homePreview) {
    homePreview.style.willChange = 'transform';
    // 添加GPU加速
    homePreview.style.transform = `translate3d(0, ${100 - dragProgress.value * 100}vh, 0)`;
  }
  
  // 开始拖动时就预加载HomeView内容
  preloadHomeViewContent();
};

// 处理拖拽移动
const dragMove = (e) => {
  if (!isDragging.value) return;
  
  // 使用requestAnimationFrame优化渲染性能
  window.requestAnimationFrame(() => {
    // 计算拖拽距离
    const currentY = e.clientY || e.touches?.[0]?.clientY || 0;
    dragY.value = startY.value - currentY;
    
    // 计算拖拽进度，使用屏幕高度的一半作为完整拖动距离
    const rawProgress = dragY.value / (window.innerHeight * 0.5);
    
    // 使用线性映射或轻微缓动，确保界面与手指同步
    const progress = Math.max(0, Math.min(1, rawProgress));
    dragProgress.value = easeOutLight(progress); // 使用轻微缓动，保持一定的平滑度
    
    // 当拖动超过10%时就预加载内容，更早地开始加载
    if (progress > 0.1 && !preloadStarted.value) {
      preloadHomeViewContent();
    }
    
    // 当到达临界点时自动进入主页
    if (progress > 0.5 && !transitionActive.value) { // 提高阈值，让用户有更多控制感
      completeTransition();
    }
  });
};

// 处理拖拽结束
const endDrag = () => {
  if (!isDragging.value) return;
  isDragging.value = false;
  
  // 恢复默认
  const homePreview = document.querySelector('.home-preview');
  if (homePreview) {
    homePreview.style.willChange = 'auto';
  }
  
  // 判断是否需要完成过渡或者恢复
  if (dragProgress.value > 0.5) { // 与dragMove中的阈值保持一致
    completeTransition();
  } else {
    resetTransition();
  }
};

// 完成过渡并跳转到主页
const completeTransition = () => {
  // 如果还没预加载，现在预加载
  if (!preloadStarted.value) {
    preloadHomeViewContent();
  }
  
  transitionActive.value = true;
  
  // 使用更平滑的动画曲线
  document.documentElement.style.setProperty('--drag-transition', '0.5s');
  document.documentElement.style.setProperty('--drag-timing-function', 'cubic-bezier(0.22, 1, 0.36, 1)');
  
  // 平滑地将进度设为1
  const startProgress = dragProgress.value;
  const startTime = performance.now();
  const duration = 500; // 500ms动画，保持适中的速度
  
  // 在动画开始前先确保HomeView可见
  if (homeViewRef.value && homeViewRef.value.setVisible) {
    homeViewRef.value.setVisible(true);
  }
  
  // 提前应用一些样式优化过渡
  const homePreview = document.querySelector('.home-preview');
  if (homePreview) {
    homePreview.style.willChange = 'transform';
    // 添加GPU加速
    homePreview.style.transform = `translate3d(0, ${100 - dragProgress.value * 100}vh, 0)`;
  }
  
  const animateProgress = (currentTime) => {
    const elapsed = currentTime - startTime;
    if (elapsed < duration) {
      const t = elapsed / duration;
      // 使用easeOutLight缓动函数，与拖动时的效果一致
      const easedT = easeOutLight(t);
      dragProgress.value = startProgress + (1 - startProgress) * easedT;
      requestAnimationFrame(animateProgress);
    } else {
      dragProgress.value = 1;
      
      // 预缓存路由组件数据，提前准备好资源
      router.isReady().then(() => {
        // 确保HomeView已完全加载后再跳转
        if (homeViewLoaded.value) {
          // 延迟一点点再跳转，确保过渡动画完成
          setTimeout(() => {
            router.push({ 
              path: '/home',
              // 添加路由元数据，标记为从splash过来，跳过进入动画
              query: { fromSplash: 'true' }
            });
          }, 50);
        } else {
          // 如果HomeView还未加载完成，等待它加载完成
          const checkLoaded = setInterval(() => {
            if (homeViewLoaded.value) {
              clearInterval(checkLoaded);
              router.push({ 
                path: '/home',
                query: { fromSplash: 'true' }
              });
            }
          }, 50);
        }
      });
    }
  };
  
  requestAnimationFrame(animateProgress);
};

// 重置过渡动画
const resetTransition = () => {
  transitionActive.value = true;
  
  // 添加动画过渡，使用更自然的曲线
  document.documentElement.style.setProperty('--drag-transition', '0.3s');
  document.documentElement.style.setProperty('--drag-timing-function', 'cubic-bezier(0.22, 1, 0.36, 1)');
  
  // 平滑地将进度设为0
  const startProgress = dragProgress.value;
  const startTime = performance.now();
  const duration = 300; // 300ms动画，更快的恢复速度
  
  const animateProgress = (currentTime) => {
    const elapsed = currentTime - startTime;
    if (elapsed < duration) {
      const t = elapsed / duration;
      // 使用easeOutLight缓动函数，与拖动时的效果一致
      const easedT = easeOutLight(t);
      dragProgress.value = startProgress - startProgress * easedT;
      requestAnimationFrame(animateProgress);
    } else {
      dragProgress.value = 0;
      
      // 重置后移除动画过渡
      setTimeout(() => {
        document.documentElement.style.setProperty('--drag-transition', '0s');
        transitionActive.value = false;
      }, 300);
    }
  };
  
  requestAnimationFrame(animateProgress);
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

// 处理触摸事件
onMounted(() => {
  // 初始化所有文字为不可见
  initWordVisibility();
  
  // 设置初始动画过渡状态
  document.documentElement.style.setProperty('--drag-transition', '0s');
  document.documentElement.style.setProperty('--drag-timing-function', 'cubic-bezier(0.22, 1, 0.36, 1)');
  
  // 添加触摸事件监听
  document.addEventListener('touchstart', startDrag, { passive: true });
  document.addEventListener('touchmove', dragMove, { passive: true });
  document.addEventListener('touchend', endDrag);
  
  // 预渲染HomeView，提前加载资源
  router.isReady().then(() => {
    // 预热路由组件
    router.resolve('/home');
    
    // 页面加载后预加载HomeView内容（延迟执行，不阻塞主页面加载）
    setTimeout(() => {
      preloadHomeViewContent();
    }, 800);
  });
  
  // 页面加载后开始文字动画
  setTimeout(showWordsSequentially, 100);
  
  // 开始轮盘动画
  updateOrbit();
});

onUnmounted(() => {
  // 移除触摸事件监听
  document.removeEventListener('touchstart', startDrag);
  document.removeEventListener('touchmove', dragMove);
  document.removeEventListener('touchend', endDrag);
  
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
  cursor: ns-resize; /* 指示可拖动 */
  user-select: none; /* 禁止文本选择 */
  -webkit-user-select: none; /* Safari */
  -moz-user-select: none; /* Firefox */
  -ms-user-select: none; /* IE10+/Edge */
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

.particles {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
}

.particle {
  position: absolute;
  background-color: var(--primary-color);
  border-radius: 50%;
  opacity: 0.3;
  box-shadow: 0 0 8px rgba(255, 220, 0, 0.4);
  transition: opacity 0.3s ease;
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
  transition: opacity 0.3s ease, transform 0.3s ease;
  will-change: transform, opacity;
}

/* 标语容器 */
.slogan-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xl);
  padding: var(--spacing-xl);
  transition: transform 0.1s linear,
              opacity 0.1s linear;
  will-change: transform, opacity;
}

.logo-container {
  margin-bottom: var(--spacing-lg);
}

.logo {
  font-size: 3rem;
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
  letter-spacing: -0.02em;
  
  span {
    font-weight: var(--font-weight-normal);
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
  opacity: 0; /* 默认不可见 */
  transform: translateY(20px);
  transition: transform 0.3s cubic-bezier(0.22, 1, 0.36, 1),
              opacity 0.3s cubic-bezier(0.22, 1, 0.36, 1);
  will-change: transform, opacity;
  
  &.visible {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 拖动提示 */
.drag-hint {
  position: absolute;
  bottom: 80px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  transition: opacity 0.1s linear,
              transform 0.1s linear;
  will-change: opacity, transform;
}

.drag-text {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  margin-bottom: var(--spacing-xs);
}

.drag-arrow {
  color: var(--primary-color);
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 20%, 50%, 80%, 100% {
    transform: translateY(0);
  }
  40% {
    transform: translateY(-10px);
  }
  60% {
    transform: translateY(-5px);
  }
}

/* 主页预览 */
.home-preview {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100vh;
  background-color: var(--background-dark);
  transform: translate3d(0, 100vh, 0);
  transition: transform 0.1s linear;
  will-change: transform;
  z-index: 10;
  
  &.transitioning {
    transition: transform var(--drag-transition, 0.5s) var(--drag-timing-function, cubic-bezier(0.22, 1, 0.36, 1));
  }
}

.home-preview-container {
  width: 100%;
  height: 100%;
  overflow: auto;
  padding-top: var(--header-height); /* 为顶部导航栏留出空间 */
  box-sizing: border-box;
  
  /* 确保内容区域与实际主页布局一致 */
  display: flex;
  flex-direction: column;
  align-items: center;
  
  /* 给 HomeView 组件添加与主页相同的布局约束 */
  :deep(.home) {
    max-width: var(--max-width-content);
    width: 100%;
    padding: var(--spacing-xl) var(--spacing-lg);
    box-sizing: border-box;
    margin: 0 auto;
  }
  
  /* 适应不同屏幕尺寸 */
  @media (max-width: 768px) {
    :deep(.home) {
      padding: var(--spacing-lg) var(--spacing-md);
    }
  }
}

/* 进度指示器 */
.progress-indicator {
  position: absolute;
  bottom: 40px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  transition: opacity 0.1s linear;
  will-change: opacity;
}

.progress-bar {
  width: 100px;
  height: 4px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background-color: var(--primary-color);
  border-radius: 2px;
  transition: width 0.05s linear;
}

.progress-text {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .logo {
    font-size: 2.5rem;
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