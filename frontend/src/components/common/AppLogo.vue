<template>
  <!-- 黄色条带过渡效果 -->
  <div v-if="showTransition" class="logo-stripe-transition">
    <div 
      v-for="i in 6" 
      :key="i" 
      class="logo-stripe"
      :class="`stripe-${i}`"
    ></div>
  </div>

  <div class="app-logo" :class="{ 'fixed-position': fixed, 'no-margin': noMargin }">
    <a 
      href="#" 
      class="logo-text"
      @click.prevent="handleLogoClick"
    >
      Zxy Space
    </a>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()

// 定义props
const props = defineProps({
  // 是否使用固定定位
  fixed: {
    type: Boolean,
    default: false
  },
  // 跳转路由，默认跳转到首页
  routeTo: {
    type: String,
    default: '/home'
  },
  // 是否无边距定位（通常用于首页，不受页面容器限制）
  noMargin: {
    type: Boolean,
    default: false
  }
})

// 过渡动画状态
const showTransition = ref(false)

// 处理logo点击
const handleLogoClick = () => {
  let targetPath = props.routeTo
  
  // 特殊逻辑：如果当前在home页面，点击logo跳转到大屏页面
  if (route.path === '/home') {
    targetPath = '/'
    // 在home页面使用淡出效果，不使用条带动画
    startFadeTransition(targetPath)
    return
  }
  // 如果已经在目标页面，不执行跳转
  else if (route.path === targetPath) {
    return
  }

  // 其他页面使用覆盖动画
  startCoverAnimation(targetPath)
}

// 开始淡出过渡（用于home页面跳转到大屏页面）
const startFadeTransition = (targetPath) => {
  // 触发home页面淡出效果
  const homeElement = document.querySelector('.home')
  if (homeElement) {
    // 确保有一个平滑的淡出过渡
    homeElement.style.transition = 'opacity 0.6s ease-out, transform 0.6s ease-out'
    homeElement.style.opacity = '0'
    homeElement.style.transform = 'scale(0.95)' // 轻微缩放效果
  }
  
  // 淡出完成后跳转
  setTimeout(() => {
    router.push({
      path: targetPath,
      query: { fadeIn: 'true' } // 告诉大屏页面使用淡入效果
    })
  }, 600) // 与淡出时间匹配
}

// 开始覆盖动画（在原页面）
const startCoverAnimation = (targetPath = props.routeTo) => {
  // 开始过渡动画
  showTransition.value = true
  
  // 第一阶段：条带进入覆盖（0.03s后开始）
  setTimeout(() => {
    const stripes = document.querySelectorAll('.logo-stripe')
    stripes.forEach(stripe => {
      stripe.classList.add('stripe-enter')
    })
  }, 30)
  
  // 第二阶段：条带完全覆盖后跳转页面（0.5s后）
  setTimeout(() => {
    // 如果跳转到splash页面（根路径），不需要logoTransition查询参数
    if (targetPath === '/') {
      router.push(targetPath)
    } else {
      router.push({
        path: targetPath,
        query: { logoTransition: 'true' }
      })
    }
  }, 500)
}

// 开始展开动画（在新页面）
const startRevealAnimation = () => {
  // 第一阶段：中间条带退场（0.1s后开始）
  setTimeout(() => {
    const middleStripes = document.querySelectorAll('.stripe-2, .stripe-5')
    middleStripes.forEach(stripe => {
      stripe.classList.add('stripe-exit-middle')
    })
  }, 100)
  
  // 第二阶段：上下条带退场（0.17s后，保持时间差）
  setTimeout(() => {
    const outerStripes = document.querySelectorAll('.stripe-1, .stripe-3, .stripe-4, .stripe-6')
    outerStripes.forEach(stripe => {
      stripe.classList.add('stripe-exit-outer')
    })
  }, 170)
  
  // 第三阶段：清理动画并移除查询参数（0.45s后）
  setTimeout(() => {
    showTransition.value = false
    // 清理URL中的查询参数
    if (route.query.logoTransition) {
      router.replace({ 
        path: route.path, 
        query: { ...route.query, logoTransition: undefined } 
      })
    }
  }, 450)
}

// 组件挂载时检查是否需要播放展开动画
onMounted(() => {
  if (route.query.logoTransition === 'true') {
    // 显示条带（跳转时条带应该已经覆盖屏幕）
    showTransition.value = true
    // 确保条带在覆盖状态
    nextTick(() => {
      const stripes = document.querySelectorAll('.logo-stripe')
      stripes.forEach(stripe => {
        stripe.classList.add('stripe-enter')
      })
             // 等待页面完全渲染后开始展开动画
       setTimeout(() => {
         startRevealAnimation()
       }, 50)
    })
  }
})
</script>

<style lang="scss" scoped>
.app-logo {
  position: absolute;
  top: var(--spacing-md);
  left: var(--spacing-sm);
  z-index: 100000; /* 确保在所有内容之上，包括黄色条带 */
  
  &.fixed-position {
    position: fixed;
  }
  
  &.no-margin {
    position: fixed !important; /* 强制固定定位 */
    top: 20px !important; /* 直接使用固定值，不受页面边距影响 */
    left: 20px !important; /* 直接使用固定值，紧贴屏幕左边 */
    z-index: 999999 !important; /* 提高层级确保显示在最顶层 */
    
    .logo-text {
      /* 无边距模式下的特殊样式 */
      transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
      
      &:hover {
        /* 在无边距模式下提供更强的视觉反馈 */
        text-shadow: 0 6px 20px rgba(255, 220, 0, 0.8);
        transform: scale(1.08);
      }
    }
  }
  
  .logo-text {
    font-size: var(--font-size-xl);
    font-weight: var(--font-weight-bold);
    font-style: italic;
    color: var(--primary-color);
    text-decoration: none;
    transition: all 0.3s cubic-bezier(0.22, 1, 0.36, 1);
    display: inline-block;
    letter-spacing: -0.01em;
    text-shadow: 0 2px 8px rgba(255, 220, 0, 0.3);
    
    &:hover {
      color: var(--text-primary);
      text-shadow: 0 4px 16px rgba(255, 220, 0, 0.6);
      transform: scale(1.05);
    }
  }
}

/* 响应式设计 */
@media (min-width: 768px) {
  .app-logo {
    top: var(--spacing-lg);
    left: var(--spacing-md);
    
    &.no-margin {
      top: 24px !important; /* 桌面端稍微增加一点顶部间距 */
      left: 24px !important; /* 桌面端稍微增加一点左边间距 */
    }
  }
  
  .logo-text {
    font-size: var(--font-size-xxl);
  }
}

@media (max-width: 768px) {
  .app-logo {
    top: var(--spacing-md);
    left: var(--spacing-sm);
    
    &.no-margin {
      top: 16px !important; /* 移动端较小的间距 */
      left: 16px !important; /* 移动端较小的间距 */
    }
  }
  
  .logo-text {
    font-size: var(--font-size-xl);
  }
}

@media (max-width: 480px) {
  .app-logo {
    top: var(--spacing-md);
    left: var(--spacing-sm);
    
    &.no-margin {
      top: 12px !important; /* 小屏幕更紧凑的间距 */
      left: 12px !important; /* 小屏幕更紧凑的间距 */
    }
  }
  
  .logo-text {
    font-size: var(--font-size-xl);
  }
}

/* 黄色条带过渡动画样式 */
.logo-stripe-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 999999; /* 确保在所有内容之上 */
  pointer-events: none;
  overflow: hidden;
}

.logo-stripe {
  position: absolute;
  width: 100vw;
  height: calc(100vh / 3 + 2px); /* 每个条带高度稍大一些，确保完全覆盖避免缝隙 */
  background: var(--primary-color);
  will-change: transform;
  transition: transform 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

/* 条带初始位置 - 左边3个从左出，右边3个从右出，调整位置确保无缝覆盖 */
.stripe-1 {
  top: 0; /* 左上条带 */
  transform: translateX(-100%); /* 从左边进入 */
}

.stripe-2 {
  top: calc(100vh / 3 - 1px); /* 左中条带，稍微向上以确保与上条带重叠 */
  transform: translateX(-100%); /* 从左边进入 */
}

.stripe-3 {
  top: calc(100vh / 3 * 2 - 2px); /* 左下条带，向上移动以确保与中条带重叠 */
  transform: translateX(-100%); /* 从左边进入 */
}

.stripe-4 {
  top: 0; /* 右上条带 */
  transform: translateX(100%); /* 从右边进入 */
}

.stripe-5 {
  top: calc(100vh / 3 - 1px); /* 右中条带，稍微向上以确保与上条带重叠 */
  transform: translateX(100%); /* 从右边进入 */
}

.stripe-6 {
  top: calc(100vh / 3 * 2 - 2px); /* 右下条带，向上移动以确保与中条带重叠 */
  transform: translateX(100%); /* 从右边进入 */
}

/* 条带进入动画 - 上下四条先进入，中间条带后进入 */
.logo-stripe.stripe-enter.stripe-1 {
  transform: translateX(-50%); /* 左上条带移动到中心，右边缘到达屏幕中心 */
  transition-delay: 0s; /* 左上，先进入 */
}

.logo-stripe.stripe-enter.stripe-2 {
  transform: translateX(-50%); /* 左中条带移动到中心，右边缘到达屏幕中心 */
  transition-delay: 0.07s; /* 左中，后进入 */
}

.logo-stripe.stripe-enter.stripe-3 {
  transform: translateX(-50%); /* 左下条带移动到中心，右边缘到达屏幕中心 */
  transition-delay: 0s; /* 左下，先进入 */
}

.logo-stripe.stripe-enter.stripe-4 {
  transform: translateX(50%); /* 右上条带移动到中心，左边缘到达屏幕中心 */
  transition-delay: 0s; /* 右上，先进入 */
}

.logo-stripe.stripe-enter.stripe-5 {
  transform: translateX(50%); /* 右中条带移动到中心，左边缘到达屏幕中心 */
  transition-delay: 0.07s; /* 右中，后进入 */
}

.logo-stripe.stripe-enter.stripe-6 {
  transform: translateX(50%); /* 右下条带移动到中心，左边缘到达屏幕中心 */
  transition-delay: 0s; /* 右下，先进入 */
}

/* 中间条带退场动画 - 第2条(左中)、第5条(右中)先退场 */
.logo-stripe.stripe-exit-middle {
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.logo-stripe.stripe-exit-middle.stripe-2 {
  transform: translateX(-100%); /* 第2条(左中)从-50%位置退回左边-100% */
  transition-delay: 0s; /* 先退场 */
}

.logo-stripe.stripe-exit-middle.stripe-5 {
  transform: translateX(100%); /* 第5条(右中)从50%位置退回右边100% */
  transition-delay: 0s; /* 先退场 */
}

/* 上下四条退场动画 - 第1,3条回左边，第4,6条回右边，一致退场 */
.logo-stripe.stripe-exit-outer {
  transition: transform 0.3s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.logo-stripe.stripe-exit-outer.stripe-1 {
  transform: translateX(-100%); /* 第1条(左上)从-50%位置退回左边-100% */
  transition-delay: 0s; /* 一致退场 */
}

.logo-stripe.stripe-exit-outer.stripe-3 {
  transform: translateX(-100%); /* 第3条(左下)从-50%位置退回左边-100% */
  transition-delay: 0s; /* 一致退场 */
}

.logo-stripe.stripe-exit-outer.stripe-4 {
  transform: translateX(100%); /* 第4条(右上)从50%位置退回右边100% */
  transition-delay: 0s; /* 一致退场 */
}

.logo-stripe.stripe-exit-outer.stripe-6 {
  transform: translateX(100%); /* 第6条(右下)从50%位置退回右边100% */
  transition-delay: 0s; /* 一致退场 */
}
</style> 