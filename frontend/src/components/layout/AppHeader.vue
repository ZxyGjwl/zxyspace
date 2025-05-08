<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '@/store/user';

const router = useRouter();
const userStore = useUserStore();

const isLoggedIn = computed(() => userStore.isLoggedIn);
const activeIndex = ref('1');

const handleSelect = (key) => {
  switch (key) {
    case '1':
      router.push('/');
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
    <el-menu
      :default-active="activeIndex"
      class="el-menu-nav"
      mode="horizontal"
      @select="handleSelect"
      :ellipsis="false"
    >
      <div class="logo-container">
        <router-link to="/">
          <h1 class="logo">ZXY Space</h1>
        </router-link>
      </div>
      <div class="menu-items">
        <el-menu-item index="1">首页</el-menu-item>
        <el-menu-item index="2">博客</el-menu-item>
        <el-menu-item index="3">关于</el-menu-item>
      </div>
      <div class="right-section">
        <template v-if="isLoggedIn">
          <el-dropdown>
            <el-avatar :size="32" :src="userStore.userInfo?.avatar || ''">
              {{ userStore.userInfo?.username?.substring(0, 1).toUpperCase() || 'U' }}
            </el-avatar>
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
          <el-button type="primary" size="small" @click="router.push('/login')">
            登录
          </el-button>
          <el-button size="small" @click="router.push('/register')">
            注册
          </el-button>
        </template>
      </div>
    </el-menu>
  </div>
</template>

<style scoped>
.header-container {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  position: fixed;
  top: 0;
  width: 100%;
  z-index: 100;
  background-color: #fff;
}

.el-menu-nav {
  display: flex;
  justify-content: space-between;
  height: 60px;
  padding: 0 20px;
}

.logo-container {
  display: flex;
  align-items: center;
}

.logo {
  margin: 0;
  color: #409EFF;
  font-size: 1.5rem;
  font-weight: 700;
}

.menu-items {
  flex-grow: 1;
  display: flex;
  justify-content: center;
}

.right-section {
  display: flex;
  align-items: center;
  gap: 10px;
}

a {
  text-decoration: none;
}
</style> 