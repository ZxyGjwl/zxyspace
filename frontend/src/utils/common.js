// 通用工具函数

// 格式化日期
export const formatDate = (dateString) => {
  const date = new Date(dateString)
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

// 模拟博客数据 - 实际应用中应从后端API获取
export const mockPosts = [
  {
    id: 1,
    title: '如何使用Vue3构建现代化前端应用',
    excerpt: 'Vue3带来了许多新特性，包括Composition API、Teleport、Fragments等...',
    content: 'Vue3是Vue.js框架的最新版本，它带来了许多令人兴奋的新特性和改进。在这篇文章中，我们将深入探讨如何使用Vue3构建现代化的前端应用...',
    createdAt: '2023-04-15T10:30:00',
    category: '前端开发',
    author: '张三',
    coverImage: 'https://picsum.photos/id/0/600/400',
    published: true,
    views: 1250,
    likes: 89,
    tags: [
      { id: 1, name: 'Vue3' },
      { id: 2, name: 'JavaScript' },
      { id: 3, name: '前端开发' }
    ],
    comments: [
      {
        id: 1,
        author: '李四',
        content: '非常实用的教程，感谢分享！',
        date: '2023-04-16T09:20:00',
        avatar: 'https://randomuser.me/api/portraits/men/2.jpg'
      }
    ]
  },
  {
    id: 2,
    title: 'Spring Boot最佳实践',
    excerpt: '本文将介绍Spring Boot开发中的一些常见问题和解决方案...',
    content: 'Spring Boot作为Java生态系统中最受欢迎的框架之一，为开发者提供了快速构建企业级应用的能力...',
    createdAt: '2023-04-10T14:20:00',
    category: '后端开发',
    author: '李四',
    coverImage: 'https://picsum.photos/id/1/600/400',
    published: true,
    views: 980,
    likes: 67,
    tags: [
      { id: 4, name: 'Spring Boot' },
      { id: 5, name: 'Java' },
      { id: 6, name: '后端开发' }
    ],
    comments: []
  },
  {
    id: 3,
    title: '我的旅行日记：杭州西湖',
    excerpt: '上周末去了杭州，游览了风景如画的西湖，体验了当地的文化和美食...',
    content: '西湖，被誉为"人间天堂"，确实名副其实。这次杭州之行让我深深感受到了江南水乡的独特魅力...',
    createdAt: '2023-04-05T08:15:00',
    category: '生活随笔',
    author: '王五',
    coverImage: 'https://picsum.photos/id/26/600/400',
    published: true,
    views: 756,
    likes: 43,
    tags: [
      { id: 7, name: '旅行' },
      { id: 8, name: '杭州' },
      { id: 9, name: '生活' }
    ],
    comments: []
  },
  {
    id: 4,
    title: 'Docker入门指南',
    excerpt: 'Docker可以让开发者打包他们的应用以及依赖包到一个轻量级、可移植的容器中...',
    content: 'Docker是一个开源的应用容器引擎，让开发者可以打包他们的应用以及依赖包到一个可移植的容器中...',
    createdAt: '2023-03-28T16:45:00',
    category: '运维',
    author: '赵六',
    coverImage: 'https://picsum.photos/id/60/600/400',
    published: true,
    views: 1120,
    likes: 78,
    tags: [
      { id: 10, name: 'Docker' },
      { id: 11, name: '容器化' },
      { id: 12, name: '运维' }
    ],
    comments: []
  },
  {
    id: 5,
    title: 'JavaScript异步编程详解',
    excerpt: '从回调函数到Promise，再到async/await，JavaScript的异步编程模式经历了怎样的演变...',
    content: 'JavaScript的异步编程是前端开发中的重要概念。从最初的回调函数，到Promise的出现，再到async/await的普及...',
    createdAt: '2023-03-20T09:00:00',
    category: '前端开发',
    author: '张三',
    coverImage: 'https://picsum.photos/id/3/600/400',
    published: true,
    views: 892,
    likes: 56,
    tags: [
      { id: 2, name: 'JavaScript' },
      { id: 13, name: '异步编程' },
      { id: 14, name: 'Promise' }
    ],
    comments: []
  },
  {
    id: 6,
    title: '数据结构与算法：二叉树遍历',
    excerpt: '二叉树是计算机科学中常用的数据结构，了解如何遍历二叉树对于解决许多问题至关重要...',
    content: '二叉树是一种重要的数据结构，在计算机科学中有着广泛的应用。掌握二叉树的遍历方法是学习数据结构的基础...',
    createdAt: '2023-03-15T11:30:00',
    category: '算法',
    author: '李四',
    coverImage: 'https://picsum.photos/id/94/600/400',
    published: true,
    views: 634,
    likes: 34,
    tags: [
      { id: 15, name: '数据结构' },
      { id: 16, name: '算法' },
      { id: 17, name: '二叉树' }
    ],
    comments: []
  }
]

// 根据ID获取文章
export const getPostById = (id) => {
  return mockPosts.find(post => post.id === parseInt(id))
}

// 获取分类列表
export const getCategories = () => {
  return [...new Set(mockPosts.map(post => post.category))]
}

// 获取标签列表
export const getTags = () => {
  const allTags = mockPosts.flatMap(post => post.tags || [])
  return allTags.filter((tag, index, self) => 
    index === self.findIndex(t => t.id === tag.id)
  )
} 