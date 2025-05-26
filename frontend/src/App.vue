<script setup>
import AppLayout from './components/layout/AppLayout.vue';
import TechBackground from './components/TechBackground.vue';
import { ref, computed, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const showLayout = computed(() => route.path !== '/');

// 在组件挂载时强制应用暗色主题
onMounted(() => {
  // 确保文档根元素有dark类
  document.documentElement.classList.add('dark');
  
  // 添加全局样式覆盖，确保背景色统一
  const styleEl = document.createElement('style');
  styleEl.textContent = `
    html, body, #app {
      background-color: var(--background-dark) !important;
    }
    
    .el-main, .el-container, .el-aside, .el-header, .el-footer {
      background-color: var(--background-dark) !important;
    }
    
    .el-table, .el-table tr, .el-table th {
      background-color: var(--background-card) !important;
      color: var(--text-primary) !important;
    }
    
    .router-view-container {
      background-color: var(--background-dark) !important;
    }
  `;
  document.head.appendChild(styleEl);
});
</script>

<template>
  <TechBackground v-if="showLayout" />
  <AppLayout v-if="showLayout" />
  <router-view v-else />
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Noto+Sans+SC:wght@300;400;500;700&family=JetBrains+Mono:wght@400;600&display=swap');
@import './styles/main.scss';

/* 全局工具类 */
.text-center { text-align: center; }
.text-left { text-align: left; }
.text-right { text-align: right; }

.mt-1 { margin-top: 0.25rem; }
.mt-2 { margin-top: 0.5rem; }
.mt-3 { margin-top: 1rem; }
.mt-4 { margin-top: 1.5rem; }
.mt-5 { margin-top: 3rem; }

.mb-1 { margin-bottom: 0.25rem; }
.mb-2 { margin-bottom: 0.5rem; }
.mb-3 { margin-bottom: 1rem; }
.mb-4 { margin-bottom: 1.5rem; }
.mb-5 { margin-bottom: 3rem; }

.ml-1 { margin-left: 0.25rem; }
.ml-2 { margin-left: 0.5rem; }
.ml-3 { margin-left: 1rem; }
.ml-4 { margin-left: 1.5rem; }
.ml-5 { margin-left: 3rem; }

.mr-1 { margin-right: 0.25rem; }
.mr-2 { margin-right: 0.5rem; }
.mr-3 { margin-right: 1rem; }
.mr-4 { margin-right: 1.5rem; }
.mr-5 { margin-right: 3rem; }

.mx-auto {
  margin-left: auto;
  margin-right: auto;
}

.flex { display: flex; }
.flex-col { flex-direction: column; }
.items-center { align-items: center; }
.justify-center { justify-content: center; }
.justify-between { justify-content: space-between; }

.gap-1 { gap: 0.25rem; }
.gap-2 { gap: 0.5rem; }
.gap-3 { gap: 1rem; }
.gap-4 { gap: 1.5rem; }
.gap-5 { gap: 3rem; }

/* 科技感动画样式类 */
.tech-glow {
  position: relative;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -2px;
    left: 0;
    width: 100%;
    height: 1px;
    background: linear-gradient(90deg, transparent, var(--primary-color), transparent);
    animation: glow 2s infinite;
  }
}

.tech-badge {
  background-color: var(--primary-color);
  color: var(--background-dark);
  padding: 0.2rem 0.6rem;
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 600;
  display: inline-block;
  box-shadow: 0 0 10px rgba(255, 220, 0, 0.3);
}

/* 全局暗色主题覆盖 */
html, body, #app {
  background-color: var(--background-dark);
  color: var(--text-primary);
}

/* 处理可能导致白色背景的内置组件 */
.el-popper, .el-select-dropdown, .el-picker-panel {
  background-color: var(--background-card) !important;
  border-color: var(--border-color) !important;
  color: var(--text-primary) !important;
}

.el-date-table td, .el-month-table td, .el-year-table td {
  color: var(--text-primary) !important;
}

.el-date-table td.current:not(.disabled) span {
  background-color: var(--primary-color) !important;
  color: var(--background-dark) !important;
}
</style>