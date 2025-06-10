import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import BlogView from '../views/BlogView.vue'
import BlogDetailView from '../views/BlogDetailView.vue'
import SplashView from '../views/SplashView.vue'
import TechStackView from '../views/TechStackView.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'splash',
      component: SplashView
    },
    {
      path: '/home',
      name: 'home',
      component: HomeView,
      meta: {
        skipTransition: true
      }
    },
    {
      path: '/blog',
      name: 'blog',
      component: BlogView
    },
    {
      path: '/blog/:id',
      name: 'blog-detail',
      component: BlogDetailView
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../views/AboutView.vue'),
      meta: {
        skipTransition: true,
        diagonalTransition: true
      }
    },
    {
      path: '/tech-stack',
      name: 'tech-stack',
      component: TechStackView,
      meta: {
        skipTransition: true,
        diagonalTransition: true
      }
    }
  ],
  scrollBehavior(to, from, savedPosition) {
    if (from.path === '/' && to.path === '/home') {
      return { top: 0, left: 0 }
    }
    return savedPosition || { top: 0, left: 0 }
  }
})

export default router