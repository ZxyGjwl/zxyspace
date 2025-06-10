<script setup>
import { useRoute } from 'vue-router';
import { ref, watch, computed } from 'vue';

const route = useRoute();
const previousRoute = ref(null);
const showTransitionBar = ref(false);

// 检查是否从 SplashView 过来
const isFromSplash = computed(() => {
  return route.query.fromSplash === 'true';
});

// 检查是否在主页且显示过渡动画
const isHomeWithTransition = computed(() => {
  return route.path === '/home' && (
    route.query.showStripeReveal === 'true' || 
    route.query.fromSplash === 'true'
  );
});

// 过渡动画钩子函数
const beforeEnter = (el) => {
  // 如果是从SplashView过来或路由标记为跳过过渡，直接显示
  if (previousRoute.value === '/' || isFromSplash.value || !previousRoute.value || route.meta.skipTransition) {
    el.style.opacity = '1';
    el.style.transform = 'translateY(0)';
  } else {
    el.style.opacity = '0';
    el.style.transform = 'translateY(10px)';
  }
};

const enter = (el, done) => {
  // 强制重绘
  el.offsetHeight;
  
  // 如果是从SplashView过来或路由标记为跳过过渡，直接完成
  if (previousRoute.value === '/' || isFromSplash.value || !previousRoute.value || route.meta.skipTransition) {
    done();
    return;
  }
  
  // 应用过渡效果
  el.style.transition = 'opacity 0.3s cubic-bezier(0.16, 1, 0.3, 1), transform 0.3s cubic-bezier(0.16, 1, 0.3, 1)';
  el.style.opacity = '1';
  el.style.transform = 'translateY(0)';
  
  // 监听过渡完成
  el.addEventListener('transitionend', done, { once: true });
};

const afterEnter = (el) => {
  // 清理过渡样式
  el.style.transition = '';
  el.style.opacity = '';
  el.style.transform = '';
};

// 监听路由变化
watch(() => route.path, (newPath, oldPath) => {
  previousRoute.value = oldPath;
}, { immediate: true });

// 监听路由变化，触发黄色条带动画
watch(route, (to, from) => {
  if (to.name === 'About' && from.name === 'Home') {
    showTransitionBar.value = true;
    setTimeout(() => {
      showTransitionBar.value = false;
    }, 800);
  }
});
</script>

<template>
  <div id="app-layout" class="app-layout">
    <!-- 黄色条带动画 -->
    <div class="transition-bar" :class="{ 'bar-animate': showTransitionBar }"></div>
    
    <main class="main-content full-viewport">
      <router-view v-slot="{ Component, route }">
        <transition 
          v-if="!route.meta.diagonalTransition"
          :name="(route.meta.skipTransition || isFromSplash) ? 'none' : 'page'" 
          :mode="(route.meta.skipTransition || isFromSplash) ? '' : 'out-in'"
          @before-enter="beforeEnter" 
          @enter="enter" 
          @after-enter="afterEnter"
        >
          <component :is="Component" />
        </transition>
        <component v-else :is="Component" />
      </router-view>
    </main>
  </div>
</template>

<style lang="scss" scoped>
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  position: relative;
  z-index: 1;
}

.main-content {
  flex: 1;
  position: relative;
  
  &.full-viewport {
    margin-top: 0;
    padding: 0;
    max-width: 100%;
    height: 100vh;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 1;
  }
}

/* 页面过渡动画 */
.page-enter-active,
.page-leave-active {
  transition: opacity 0.3s cubic-bezier(0.16, 1, 0.3, 1), 
              transform 0.3s cubic-bezier(0.16, 1, 0.3, 1);
  will-change: opacity, transform;
  backface-visibility: hidden;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 无过渡动画 */
.none-enter-active,
.none-leave-active {
  transition: none;
}

.none-enter-from,
.none-leave-to {
  opacity: 1;
}

/* 黄色条带动画 */
.transition-bar {
  position: fixed;
  top: 0;
  right: 100%;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), #ffd700);
  z-index: 9999;
  transition: none;
}

.transition-bar.bar-animate {
  animation: slideBarLeft 0.8s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

@keyframes slideBarLeft {
  0% {
    right: 100%;
  }
  50% {
    right: -10%;
    width: 110%;
  }
  100% {
    right: -100%;
    width: 100%;
  }
}

/* About 页面专用过渡动画 */
.page-enter-active {
  transition: all 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.page-leave-active {
  transition: all 0.3s ease;
}

.page-enter-from {
  transform: translateX(100%);
  opacity: 0;
}

.page-leave-to {
  transform: translateX(-50px);
  opacity: 0;
}
</style> 