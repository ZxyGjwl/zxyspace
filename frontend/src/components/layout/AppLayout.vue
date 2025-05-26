<script setup>
import AppHeader from './AppHeader.vue';
import AppFooter from './AppFooter.vue';
import { useRoute } from 'vue-router';
import { ref, watch, computed } from 'vue';

const route = useRoute();
const previousRoute = ref(null);

// 检查是否从 SplashView 过来
const isFromSplash = computed(() => {
  return route.query.fromSplash === 'true';
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
</script>

<template>
  <div class="app-container">
    <div class="page-background">
      <div class="tech-background"></div>
      <div class="particles"></div>
    </div>
    
    <AppHeader />
    
    <main class="main-content">
      <router-view v-slot="{ Component, route }">
        <transition 
          :name="(route.meta.skipTransition || isFromSplash) ? 'none' : 'page'" 
          :mode="(route.meta.skipTransition || isFromSplash) ? '' : 'out-in'"
          @before-enter="beforeEnter" 
          @enter="enter" 
          @after-enter="afterEnter"
        >
          <component :is="Component" />
        </transition>
      </router-view>
    </main>
    
    <AppFooter />
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
  margin-top: var(--header-height);
  padding: var(--spacing-xl) var(--spacing-lg);
  max-width: var(--max-width-content);
  width: 100%;
  margin-left: auto;
  margin-right: auto;
  position: relative;
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

// 适应不同屏幕尺寸
@media (max-width: 768px) {
  .main-content {
    padding: var(--spacing-lg) var(--spacing-md);
  }
}
</style> 