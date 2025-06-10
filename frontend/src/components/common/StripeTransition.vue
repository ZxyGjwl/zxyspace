<template>
  <div class="stripe-transition" 
       :class="{ 
         'active': active, 
         'revealing': revealing,
         'covered': covered
       }"
       v-if="visible">
    <div 
      v-for="i in 6" 
      :key="i" 
      class="stripe"
    ></div>
  </div>
</template>

<script setup>
defineProps({
  active: {
    type: Boolean,
    default: false
  },
  revealing: {
    type: Boolean,
    default: false
  },
  visible: {
    type: Boolean,
    default: true
  },
  covered: {
    type: Boolean,
    default: false
  }
})
</script>

<style lang="scss" scoped>
.stripe-transition {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  pointer-events: none;
  z-index: 9999;
  
  /* 启用硬件加速和优化 */
  will-change: transform, opacity;
  transform: translateZ(0);
  backface-visibility: hidden;
  
  /* 当active为true时播放覆盖动画（从SplashView使用） */
  &.active .stripe {
    /* 从初始位置开始覆盖动画 */
    transform: translateZ(0) translateY(var(--initial-position));
    
    /* 中心对称式进入：最外侧（1,6）先进入，然后（2,5），最后（3,4） */
    &:nth-child(1) { animation: stripeCover 0.35s 0.0s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; }  /* 最左边，与最右边同时 */
    &:nth-child(6) { animation: stripeCover 0.35s 0.0s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; }  /* 最右边，最快 */
    &:nth-child(2) { animation: stripeCover 0.35s 0.08s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 左中，与右中同时 */
    &:nth-child(5) { animation: stripeCover 0.35s 0.08s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 右中，其次 */
    &:nth-child(3) { animation: stripeCover 0.35s 0.16s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 左右，与右左同时 */
    &:nth-child(4) { animation: stripeCover 0.35s 0.16s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 右左，最慢 */
  }
  
  /* 当covered为true时，立即让条带处于覆盖状态（无动画） */
  &.covered .stripe {
    transform: translateZ(0) translateY(0);
  }
  
  /* 当revealing为true时，播放消失动画（从HomeView使用） */
  &.revealing .stripe {
    /* 确保条带在覆盖状态，然后开始消失动画 */
    transform: translateZ(0) translateY(0);
    
    /* 中心对称式退场：最外侧（1,6）先退场，然后（2,5），最后（3,4） */
    &:nth-child(1) { animation: stripeReveal 0.4s 0s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; }    /* 最左边，与最右边同时 */
    &:nth-child(6) { animation: stripeReveal 0.4s 0s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; }    /* 最右边，最快 */
    &:nth-child(2) { animation: stripeReveal 0.4s 0.08s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 左中，与右中同时 */
    &:nth-child(5) { animation: stripeReveal 0.4s 0.08s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 右中，其次 */
    &:nth-child(3) { animation: stripeReveal 0.4s 0.16s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 左右，与右左同时 */
    &:nth-child(4) { animation: stripeReveal 0.4s 0.16s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards; } /* 右左，最慢 */
  }
  
  /* 当既不是active、revealing也不是covered时，确保条带在初始位置 */
  &:not(.active):not(.revealing):not(.covered) .stripe {
    transform: translateZ(0) translateY(var(--initial-position));
  }
}

.stripe {
  position: absolute;
  top: 0;
  width: calc(100vw / 6);
  height: 100vh;
  background: var(--primary-color);
  
  /* 性能优化 */
  will-change: transform;
  transform: translateZ(0) translateY(var(--initial-position));
  backface-visibility: hidden;
  contain: layout style paint;
  
  /* 前3个条带从上方进入 */
  &:nth-child(1) { 
    left: 0%; 
    --initial-position: -100vh;
  }
  &:nth-child(2) { 
    left: calc(100vw / 6 * 1); 
    --initial-position: -100vh;
  }
  &:nth-child(3) { 
    left: calc(100vw / 6 * 2); 
    --initial-position: -100vh;
  }
  
  /* 后3个条带从下方进入 */
  &:nth-child(4) { 
    left: calc(100vw / 6 * 3); 
    --initial-position: 100vh;
  }
  &:nth-child(5) { 
    left: calc(100vw / 6 * 4); 
    --initial-position: 100vh;
  }
  &:nth-child(6) { 
    left: calc(100vw / 6 * 5); 
    --initial-position: 100vh;
  }
}

/* 优化的条带覆盖动画 */
@keyframes stripeCover {
  0% {
    transform: translateZ(0) translateY(var(--initial-position));
  }
  100% {
    transform: translateZ(0) translateY(0);
  }
}

/* 优化的条带消失动画 */
@keyframes stripeReveal {
  0% {
    transform: translateZ(0) translateY(0);
  }
  100% {
    transform: translateZ(0) translateY(var(--initial-position));
  }
}
</style> 