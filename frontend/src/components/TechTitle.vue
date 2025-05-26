<template>
  <div class="tech-title-container" :class="{ 'animate': animate }">
    <h1 class="tech-title neon-text">
      <slot>{{ text }}</slot>
    </h1>
    <div class="tech-title-decoration">
      <div class="left-line"></div>
      <div class="center-dot"></div>
      <div class="right-line"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

const props = defineProps({
  text: {
    type: String,
    default: ''
  },
  animate: {
    type: Boolean,
    default: true
  }
});

const isVisible = ref(false);

onMounted(() => {
  setTimeout(() => {
    isVisible.value = true;
  }, 100);
});
</script>

<style lang="scss" scoped>
.tech-title-container {
  margin: 2rem 0;
  text-align: center;
  opacity: 0;
  transform: translateY(20px);
  
  &.animate {
    animation: title-appear 0.8s forwards ease-out;
  }
}

@keyframes title-appear {
  0% {
    opacity: 0;
    transform: translateY(20px);
  }
  100% {
    opacity: 1;
    transform: translateY(0);
  }
}

.tech-title {
  font-size: 2.5rem;
  font-weight: 600;
  color: var(--primary-color);
  margin-bottom: 0.5rem;
  letter-spacing: 1px;
  position: relative;
  display: inline-block;
  
  &::before {
    content: attr(data-text);
    position: absolute;
    left: 0;
    top: 0;
    width: 100%;
    height: 100%;
    z-index: -1;
    filter: blur(8px);
    opacity: 0.7;
    color: var(--primary-color);
  }
}

.tech-title-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0.5rem auto;
  width: 80%;
  max-width: 500px;
  
  .left-line, .right-line {
    height: 2px;
    flex: 1;
    background: linear-gradient(90deg, transparent, var(--primary-color));
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background: var(--primary-color);
      transform: scaleX(0);
      transform-origin: left;
      animation: line-expand 2s forwards ease-out;
    }
  }
  
  .right-line {
    background: linear-gradient(90deg, var(--primary-color), transparent);
    
    &::after {
      transform-origin: right;
    }
  }
  
  .center-dot {
    width: 12px;
    height: 12px;
    background-color: var(--primary-color);
    border-radius: 50%;
    margin: 0 10px;
    position: relative;
    
    &::after {
      content: '';
      position: absolute;
      top: -4px;
      left: -4px;
      width: 20px;
      height: 20px;
      border-radius: 50%;
      border: 1px solid var(--primary-color);
      animation: pulse 2s infinite;
    }
  }
}

@keyframes line-expand {
  0% {
    transform: scaleX(0);
  }
  100% {
    transform: scaleX(1);
  }
}

// 响应式调整
@media (max-width: 768px) {
  .tech-title {
    font-size: 1.8rem;
  }
  
  .tech-title-decoration {
    width: 90%;
  }
}
</style>