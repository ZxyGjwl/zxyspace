import { defineStore } from 'pinia'
import axios from 'axios'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: localStorage.getItem('token') || null,
    userInfo: JSON.parse(localStorage.getItem('userInfo')) || null,
  }),
  
  getters: {
    isLoggedIn: (state) => !!state.token,
    isAdmin: (state) => state.userInfo?.role === 'ADMIN',
    userFullName: (state) => {
      if (!state.userInfo) return ''
      return `${state.userInfo.firstName} ${state.userInfo.lastName}`
    }
  },
  
  actions: {
    setToken(token) {
      this.token = token;
      localStorage.setItem('token', token);
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    },
    
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
      localStorage.setItem('userInfo', JSON.stringify(userInfo));
    },
    
    async login(credentials) {
      try {
        const response = await axios.post('/api/auth/login', credentials);
        const { token, user } = response.data;
        this.setToken(token);
        this.setUserInfo(user);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          message: error.response?.data?.message || '登录失败，请检查用户名和密码'
        };
      }
    },
    
    async register(userData) {
      try {
        const response = await axios.post('/api/auth/register', userData);
        const { token, user } = response.data;
        this.setToken(token);
        this.setUserInfo(user);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          message: error.response?.data?.message || '注册失败，请稍后重试'
        };
      }
    },
    
    async fetchUserInfo() {
      if (!this.token) return;
      
      try {
        const response = await axios.get('/api/auth/me');
        this.setUserInfo(response.data);
        return { success: true };
      } catch (error) {
        if (error.response?.status === 401) {
          this.logout();
        }
        return {
          success: false,
          message: error.response?.data?.message || '获取用户信息失败'
        };
      }
    },
    
    logout() {
      this.token = null;
      this.userInfo = null;
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      delete axios.defaults.headers.common['Authorization'];
    },
    
    async updateProfile(userData) {
      try {
        const response = await axios.put('/api/users/profile', userData);
        this.setUserInfo(response.data);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          message: error.response?.data?.message || '更新个人资料失败'
        };
      }
    },
    
    async changePassword(passwordData) {
      try {
        await axios.put('/api/users/password', passwordData);
        return { success: true };
      } catch (error) {
        return {
          success: false,
          message: error.response?.data?.message || '修改密码失败'
        };
      }
    }
  }
})