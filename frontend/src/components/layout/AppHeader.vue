<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { useUserStore } from '@/store/user';

const router = useRouter();
const route = useRoute();
const userStore = useUserStore();

const isLoggedIn = computed(() => userStore.isLoggedIn);
const activeIndex = ref('1');

// 监听路由变化，更新激活的菜单项
const updateActiveMenu = () => {
  const path = route.path;
  if (path.includes('/home')) {
    activeIndex.value = '1';
  } else if (path.includes('/blog')) {
    activeIndex.value = '2';
  } else if (path.includes('/about')) {
    activeIndex.value = '3';
  }
};

onMounted(() => {
  updateActiveMenu();
});

const handleSelect = (key) => {
  switch (key) {
    case '1':
      router.push('/home');
      break;
    case '2':
      router.push('/blog');
      break;
    case '3':
      router.push('/about');
      break;
    case '4':
      router.push('/login');
      break;
    case '5':
      userStore.logout();
      router.push('/');
      break;
  }
};
</script>

<template>
  <div class="header-container">
    <div class="navbar">
      <div class="navbar-container">
        <div class="navbar-brand">
          <router-link to="/home">
            <h1 class="logo">ZXY <span class="accent">Space</span></h1>
          </router-link>
        </div>
        <div class="navbar-nav">
          <router-link to="/home" class="nav-link" :class="{ 'active': activeIndex === '1' }">
            首页
          </router-link>
          <router-link to="/blog" class="nav-link" :class="{ 'active': activeIndex === '2' }">
            博客
          </router-link>
          <router-link to="/about" class="nav-link" :class="{ 'active': activeIndex === '3' }">
            关于
          </router-link>
        </div>
        <div class="navbar-actions">
          <template v-if="isLoggedIn">
            <el-dropdown trigger="click">
              <div class="avatar-container">
                <el-avatar :size="36" :src="userStore.userInfo?.avatar || ''">
                  {{ userStore.userInfo?.username?.substring(0, 1).toUpperCase() || 'U' }}
                </el-avatar>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="router.push('/dashboard')">
                    <el-icon><Setting /></el-icon> 控制台
                  </el-dropdown-item>
                  <el-dropdown-item @click="router.push('/profile')">
                    <el-icon><User /></el-icon> 个人资料
                  </el-dropdown-item>
                  <el-dropdown-item divided @click="userStore.logout()">
                    <el-icon><SwitchButton /></el-icon> 退出登录
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </template>
          <template v-else>
            <button class="btn btn-text" @click="router.push('/login')">
              登录
            </button>
            <button class="btn btn-primary" @click="router.push('/register')">
              注册
            </button>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<style lang="scss" scoped>
.header-container {
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 100;
}

.navbar {
  background-color: rgba(18, 18, 18, 0.8);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  height: var(--header-height);
  display: flex;
  align-items: center;
}

.navbar-container {
  max-width: var(--max-width-content);
  width: 100%;
  margin: 0 auto;
  padding: 0 var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.navbar-brand {
  display: flex;
  align-items: center;
  
  a {
    text-decoration: none;
  }
}

.logo {
  margin: 0;
  color: var(--primary-color);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-bold);
  letter-spacing: -0.01em;
  
  .accent {
    font-weight: var(--font-weight-medium);
    opacity: 0.9;
  }
}

.navbar-nav {
  display: flex;
  gap: var(--spacing-lg);
}

.nav-link {
  color: var(--text-primary);
  font-weight: var(--font-weight-medium);
  position: relative;
  padding: var(--spacing-xs) var(--spacing-sm);
  text-decoration: none;
  
  &::after {
    content: '';
    position: absolute;
    bottom: -3px;
    left: 0;
    width: 0;
    height: 2px;
    background-color: var(--primary-color);
    transition: width var(--transition-normal);
  }
  
  &:hover::after,
  &.active::after {
    width: 100%;
  }
  
  &.active {
    color: var(--primary-color);
  }
}

.navbar-actions {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.avatar-container {
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform var(--transition-fast);
  
  &:hover {
    transform: scale(1.05);
  }
  
  .el-avatar {
    border: 2px solid rgba(255, 220, 0, 0.2);
  }
}

@media (max-width: 768px) {
  .navbar-container {
    padding: 0 var(--spacing-md);
  }
  
  .navbar-nav {
    gap: var(--spacing-md);
  }
  
  .logo {
    font-size: var(--font-size-md);
  }
  
  .btn {
    padding: var(--spacing-xs) var(--spacing-sm);
    font-size: var(--font-size-sm);
  }
}
</style>