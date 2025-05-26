<template>
  <div class="tech-background-container">
    <div class="tech-grid-bg"></div>
    <div class="particles-container" ref="particlesContainer"></div>
    <div class="scan-line"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';

const particlesContainer = ref(null);
let particlesArray = [];
let animationFrame = null;

// 粒子类
class Particle {
  constructor(container) {
    this.container = container;
    this.element = document.createElement('div');
    this.element.classList.add('particle');
    this.size = Math.random() * 5 + 1;
    this.element.style.width = `${this.size}px`;
    this.element.style.height = `${this.size}px`;
    this.element.style.opacity = Math.random() * 0.5 + 0.1;
    
    // 位置
    this.x = Math.random() * 100;
    this.y = Math.random() * 100;
    this.element.style.left = `${this.x}%`;
    this.element.style.top = `${this.y}%`;
    
    // 速度和方向
    this.xVelocity = Math.random() * 0.2 - 0.1;
    this.yVelocity = Math.random() * 0.2 - 0.1;
    
    // 发光效果
    this.element.style.boxShadow = `0 0 ${this.size * 2}px rgba(255, 220, 0, 0.7)`;
    
    // 添加到容器
    this.container.appendChild(this.element);
  }
  
  update() {
    // 移动粒子
    this.x += this.xVelocity;
    this.y += this.yVelocity;
    
    // 边界检测
    if (this.x > 100 || this.x < 0) {
      this.xVelocity = -this.xVelocity;
    }
    
    if (this.y > 100 || this.y < 0) {
      this.yVelocity = -this.yVelocity;
    }
    
    // 更新位置
    this.element.style.left = `${this.x}%`;
    this.element.style.top = `${this.y}%`;
  }
  
  remove() {
    this.container.removeChild(this.element);
  }
}

// 创建粒子
const createParticles = () => {
  if (!particlesContainer.value) return;
  
  // 清除现有粒子
  particlesArray.forEach(particle => particle.remove());
  particlesArray = [];
  
  // 根据容器大小决定粒子数量
  const containerWidth = particlesContainer.value.clientWidth;
  const particleCount = Math.min(Math.floor(containerWidth / 50), 40);
  
  // 创建新粒子
  for (let i = 0; i < particleCount; i++) {
    particlesArray.push(new Particle(particlesContainer.value));
  }
};

// 动画循环
const animateParticles = () => {
  particlesArray.forEach(particle => particle.update());
  animationFrame = requestAnimationFrame(animateParticles);
};

// 处理窗口大小变化
const handleResize = () => {
  if (animationFrame) {
    cancelAnimationFrame(animationFrame);
  }
  createParticles();
  animationFrame = requestAnimationFrame(animateParticles);
};

onMounted(() => {
  createParticles();
  animationFrame = requestAnimationFrame(animateParticles);
  window.addEventListener('resize', handleResize);
});

onUnmounted(() => {
  if (animationFrame) {
    cancelAnimationFrame(animationFrame);
  }
  window.removeEventListener('resize', handleResize);
  particlesArray.forEach(particle => particle.remove());
});
</script>

<style lang="scss" scoped>
.tech-background-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: -1;
  overflow: hidden;
  background-color: var(--background-dark);
}

.particles-container {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
}

.particle {
  position: absolute;
  background-color: var(--primary-color);
  border-radius: 50%;
  opacity: 0.4;
  transition: opacity 0.3s ease;
}

.tech-grid-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-image: 
    linear-gradient(rgba(255, 220, 0, 0.03) 1px, transparent 1px),
    linear-gradient(90deg, rgba(255, 220, 0, 0.03) 1px, transparent 1px);
  background-size: 30px 30px;
}

.scan-line {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 2px;
  background-color: rgba(255, 220, 0, 0.3);
  box-shadow: 0 0 10px rgba(255, 220, 0, 0.5);
  animation: scan 8s linear infinite;
  opacity: 0.5;
}

@keyframes scan {
  0% {
    top: -5%;
  }
  100% {
    top: 105%;
  }
}
</style>