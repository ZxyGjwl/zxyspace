import { defineStore } from 'pinia'
import axios from 'axios'

export const useBlogStore = defineStore('blog', {
  state: () => ({
    posts: [],
    currentPost: null,
    categories: [],
    tags: [],
    loading: false,
    error: null,
    totalPosts: 0,
    currentPage: 1,
    pageSize: 10,
    searchQuery: '',
    selectedCategory: null,
    selectedTag: null,
  }),
  
  getters: {
    getPostById: (state) => (id) => {
      return state.posts.find(post => post.id === parseInt(id));
    },
    
    featuredPosts: (state) => {
      return state.posts
        .filter(post => post.published)
        .sort((a, b) => b.views - a.views)
        .slice(0, 5);
    },
    
    recentPosts: (state) => {
      return [...state.posts]
        .filter(post => post.published)
        .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        .slice(0, 5);
    },
    
    filteredPosts: (state) => {
      let result = [...state.posts].filter(post => post.published);
      
      if (state.searchQuery) {
        const query = state.searchQuery.toLowerCase();
        result = result.filter(post => 
          post.title.toLowerCase().includes(query) || 
          post.excerpt.toLowerCase().includes(query) ||
          post.content.toLowerCase().includes(query)
        );
      }
      
      if (state.selectedCategory) {
        result = result.filter(post => 
          post.category && post.category.id === state.selectedCategory
        );
      }
      
      if (state.selectedTag) {
        result = result.filter(post => 
          post.tags && post.tags.some(tag => tag.id === state.selectedTag)
        );
      }
      
      return result;
    },
    
    paginatedPosts: (state) => {
      const start = (state.currentPage - 1) * state.pageSize;
      const end = start + state.pageSize;
      return state.filteredPosts.slice(start, end);
    },
    
    totalPages: (state) => {
      return Math.ceil(state.filteredPosts.length / state.pageSize);
    }
  },
  
  actions: {
    async fetchPosts() {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await axios.get('/api/posts');
        this.posts = response.data;
        this.totalPosts = response.data.length;
        this.loading = false;
      } catch (error) {
        this.error = error.response?.data?.message || '获取博客列表失败';
        this.loading = false;
        console.error('Failed to fetch posts:', error);
      }
    },
    
    async fetchPostById(id) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await axios.get(`/api/posts/${id}`);
        this.currentPost = response.data;
        this.loading = false;
        return response.data;
      } catch (error) {
        this.error = error.response?.data?.message || '获取博客详情失败';
        this.loading = false;
        console.error(`Failed to fetch post with id ${id}:`, error);
        return null;
      }
    },
    
    async fetchCategories() {
      try {
        const response = await axios.get('/api/categories');
        this.categories = response.data;
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    },
    
    async fetchTags() {
      try {
        const response = await axios.get('/api/tags');
        this.tags = response.data;
      } catch (error) {
        console.error('Failed to fetch tags:', error);
      }
    },
    
    async createPost(postData) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await axios.post('/api/posts', postData);
        this.posts.push(response.data);
        this.loading = false;
        return { success: true, post: response.data };
      } catch (error) {
        this.error = error.response?.data?.message || '创建博客失败';
        this.loading = false;
        console.error('Failed to create post:', error);
        return { success: false, message: this.error };
      }
    },
    
    async updatePost(id, postData) {
      this.loading = true;
      this.error = null;
      
      try {
        const response = await axios.put(`/api/posts/${id}`, postData);
        const index = this.posts.findIndex(post => post.id === parseInt(id));
        if (index !== -1) {
          this.posts[index] = response.data;
        }
        this.currentPost = response.data;
        this.loading = false;
        return { success: true, post: response.data };
      } catch (error) {
        this.error = error.response?.data?.message || '更新博客失败';
        this.loading = false;
        console.error(`Failed to update post with id ${id}:`, error);
        return { success: false, message: this.error };
      }
    },
    
    async deletePost(id) {
      this.loading = true;
      this.error = null;
      
      try {
        await axios.delete(`/api/posts/${id}`);
        this.posts = this.posts.filter(post => post.id !== parseInt(id));
        this.loading = false;
        return { success: true };
      } catch (error) {
        this.error = error.response?.data?.message || '删除博客失败';
        this.loading = false;
        console.error(`Failed to delete post with id ${id}:`, error);
        return { success: false, message: this.error };
      }
    },
    
    async likePost(id) {
      try {
        const response = await axios.post(`/api/posts/${id}/like`);
        const index = this.posts.findIndex(post => post.id === parseInt(id));
        if (index !== -1) {
          this.posts[index].likes = response.data.likes;
        }
        if (this.currentPost && this.currentPost.id === parseInt(id)) {
          this.currentPost.likes = response.data.likes;
        }
        return { success: true };
      } catch (error) {
        console.error(`Failed to like post with id ${id}:`, error);
        return { 
          success: false, 
          message: error.response?.data?.message || '点赞失败' 
        };
      }
    },
    
    async addComment(postId, commentData) {
      try {
        const response = await axios.post(`/api/posts/${postId}/comments`, commentData);
        if (this.currentPost && this.currentPost.id === parseInt(postId)) {
          if (!this.currentPost.comments) {
            this.currentPost.comments = [];
          }
          this.currentPost.comments.push(response.data);
        }
        return { success: true, comment: response.data };
      } catch (error) {
        console.error(`Failed to add comment to post with id ${postId}:`, error);
        return { 
          success: false, 
          message: error.response?.data?.message || '评论发表失败' 
        };
      }
    },
    
    setSearchParams(params) {
      if (params.query !== undefined) this.searchQuery = params.query;
      if (params.category !== undefined) this.selectedCategory = params.category;
      if (params.tag !== undefined) this.selectedTag = params.tag;
      if (params.page !== undefined) this.currentPage = params.page;
      else this.currentPage = 1; // 搜索条件改变时重置页码
    },
    
    resetSearchParams() {
      this.searchQuery = '';
      this.selectedCategory = null;
      this.selectedTag = null;
      this.currentPage = 1;
    }
  }
})